package com.example.taskflow.domain.comment.service;

import com.example.taskflow.domain.comment.dto.CommentResponseDto;
import com.example.taskflow.domain.comment.dto.findUserNameResponseDto;
import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.comment.repository.CommentRepository;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.common.BaseTimeEntity;
import com.example.taskflow.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class CommentService extends BaseTimeEntity {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    @Transactional
    public CommentResponseDto singup(Long takeId,String userName,String content) {

        User user = userRepository.findByUsername(userName).get();
        Task task = taskRepository.findById(takeId).get();

        Comment comment = new Comment(task,user,content);
        Comment createcomment = commentRepository.save(comment);

        return new CommentResponseDto(user.getId(),task.getId(),createcomment.getContent(),
                createcomment.getIsDeleted(),createcomment.getCreatedAt(),createcomment.getModifiedAt());


    }

    public PageResponse<findUserNameResponseDto> findUserNameList(Long taskId, Pageable pageables){

        PageRequest pageable = PageRequest.of(pageables.getPageNumber(),pageables.getPageSize(),Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findAllByTaskId(taskId,pageable);

        return PageResponse.of(commentPage,findUserNameResponseDto::findUserNameDto);

    }


}