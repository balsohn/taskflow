package com.example.taskflow.domain.comment.service;

import com.example.taskflow.domain.comment.dto.CommentDeleteResponseDto;
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
import com.example.taskflow.global.exception.custom.TaskNotFoundException;
import com.example.taskflow.global.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class CommentService{
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    @Transactional
    public CommentResponseDto signUp(Long taskId,String userName,String content) {

        User user = userRepository.findByUsernameOrElseThrow(userName);
        Task task = taskRepository.findByIdWithAssigneeAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new TaskNotFoundException("존재하지 않는 태스크입니다."));

        Comment comment = new Comment(task,user,content);
        Comment createcomment = commentRepository.save(comment);

        return CommentResponseDto.commentResponsesDto(createcomment);
    }

    public PageResponse<findUserNameResponseDto> findUserNameList(String content,Long taskId, Pageable pageables){

        PageRequest pageable = PageRequest.of(pageables.getPageNumber(),pageables.getPageSize(),Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findByTaskIdAndContentContaining(taskId,content,pageable);

        return PageResponse.of(commentPage,findUserNameResponseDto::findUserNameDto);

    }

    public CommentDeleteResponseDto deleteComment(Long commentId,String userName){

        Comment comment = commentRepository.findById(commentId).
                orElseThrow(()->new TaskNotFoundException("존재하지 않는 댓글입니다."));

        if(!userName.equals(comment.getUser().getUsername())) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        comment.delete();//엔티티에 값 변동을 위해 메서드 호출 로직 > 호출 후 값변동
        return new CommentDeleteResponseDto(comment.getIsDeleted(),comment.getDeletedAt());

    }

}