package com.example.taskflow.domain.comment.service;

import com.example.taskflow.domain.comment.dto.CommentResponseDto;
import com.example.taskflow.domain.comment.dto.findUserNameResponseDto;
import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.comment.response.CommentResponse;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.common.BaseTimeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class CommentService extends BaseTimeEntity {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentResponse commentResponse;


    @Transactional
    public CommentResponseDto singup(Long id,String comments) {

        User user = userRepository.findById(id).get();
        int taskId = 1;

        Comment comment = new Comment(taskId,user,comments);
        Comment createcomment = commentResponse.save(comment);

        return new CommentResponseDto(createcomment.getComment(),user.getName(),createcomment.getTasksId(),
                createcomment.getIsDeleted(),createcomment.getCreatedAt(),createcomment.getModifiedAt());


    }

//    public List<findUserNameResponseDto>findUserNameList(String userName,
//                                                         String detail){
//        User user = userRepository.findByName(userName);
//        List<Comment> userNameList = commentResponse.findbyUser(userName);
//        List<findUserNameResponseDto> userNameResponseDtoList = userNameList
//                .stream()
//                .map(findUserNameResponseDto::findUserNameDto)
//                .toList()
//
//    }


}
