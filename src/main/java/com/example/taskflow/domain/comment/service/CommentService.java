package com.example.taskflow.domain.comment.service;

import com.example.taskflow.domain.comment.dto.CommentResponseDto;
import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class CommentService {
   private final UserRepository userRepository;
    private final TaskRepository taskRepository;


    @Transactional
    public CommentResponseDto singup(Long taskId,String detail) {

        //Task tasksId = taskRepository.findByTaskId(taskId);
        //User users = userRepository.findByUserId(taskId);

        //users.setName("이형준");
        String Name = "이형준";
        int id = 1;

        Comment comment = new Comment(id,detail);

        return new CommentResponseDto(comment.getDetail(),Name,comment.getTast(),
                comment.getIsDeleted(),comment.getCreatedAt(),comment.getModifiedAt());

    }


}
