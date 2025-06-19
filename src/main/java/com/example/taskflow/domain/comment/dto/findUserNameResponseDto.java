package com.example.taskflow.domain.comment.dto;

import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.task.dto.response.UserInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class findUserNameResponseDto{
    @NotBlank
    private final Long id;
    @NotBlank
    private final String content;
    private final Long taskId;
    private final Long userId;
    private UserInfo User;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime modifiedAt;


    public findUserNameResponseDto(Long id,String content,Long taskId,
                                   Long userId,UserInfo user,LocalDateTime createdAt,
                                   LocalDateTime modifiedAt) {
        this.id = id;
        this.content = content;
        this.taskId = taskId;
        this.userId = userId;
        this.User = user;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static findUserNameResponseDto findUserNameDto(Comment comment){
        return new findUserNameResponseDto(comment.getId(),comment.getContent(),
                comment.getTask().getId(),comment.getUser().getId(),UserInfo.fromUser(comment.getUser()),
                comment.getCreatedAt(),comment.getModifiedAt());
    }
}