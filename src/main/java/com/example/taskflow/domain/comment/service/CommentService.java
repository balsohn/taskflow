package com.example.taskflow.domain.comment.service;

import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.comment.dto.CommentResponseDto;
import com.example.taskflow.domain.comment.dto.findUserNameResponseDto;
import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.comment.repository.CommentRepository;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.common.BaseTimeEntity;
import com.example.taskflow.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class CommentService extends BaseTimeEntity {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentResponse;


    @Transactional
    public CommentResponseDto singup(Long taskId,String content) {

        User user = userRepository.findById(taskId).get();
        int tasksId = 1;

        Comment comment = new Comment(tasksId,user,content);
        Comment createcomment = commentResponse.save(comment);

        return new CommentResponseDto(createcomment.getUser().getId(),createcomment.getTasksId(),createcomment.getContent(),
                createcomment.getIsDeleted(),createcomment.getCreatedAt(),createcomment.getModifiedAt());


    }

    public PageResponse<findUserNameResponseDto> findUserNameList(String userName, String comment,int page,int size){
        User user = userRepository.findByName(userName);
        List<Comment> userNameList = commentResponse.findByUserAndContent(user,comment);

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> logs = commentResponse.findAll(pageable);


        List<findUserNameResponseDto> userNameResponseDtoList = userNameList
                .stream()
                .map(findUserNameResponseDto::findUserNameDto)
                .toList();

        return PageResponse.of(userNameResponseDtoList,logs);

    }


}
