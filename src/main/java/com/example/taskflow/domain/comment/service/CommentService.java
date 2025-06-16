package com.example.taskflow.domain.comment.service;

import com.example.taskflow.domain.comment.dto.CommentResponseDto;
import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.comment.response.CommentResponse;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.dto.UserResponseDto;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {
   private final UserRepository userRepository;
    private final TaskRepository taskRepository;


    @Transactional
    public CommentResponseDto singup(Long taskId,String detail) {

        Task tasksId = taskRepository.findByTaskId(taskId);
        User users = userRepository.findByUserId(taskId);

        users.setName("이형준");

        Comment comment = new Comment(tasksId,detail);

        return new CommentResponseDto(comment.getDetail(),users.getName(),comment.getTask().getTaskId(),
                comment.getIsDeleted(),comment.getCreatedAt(),comment.getModifiedAt());

    }


}
