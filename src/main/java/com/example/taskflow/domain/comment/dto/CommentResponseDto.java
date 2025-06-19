package com.example.taskflow.domain.comment.dto;

import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.task.dto.response.UserInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private final Long id;
    private final Long taskId;
    private final Long userId;
    private final UserInfo user;
    @NotBlank
    private final String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime modifiedAt;

    public CommentResponseDto(Long id,String content,Long taskId,Long userId,UserInfo user,
                              LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.content = content;
        this.taskId = taskId;
        this.userId = userId;
        this.user = user;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
    public static CommentResponseDto commentResponsesDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getTask().getId(),
                comment.getUser().getId(),
                UserInfo.fromUser(comment.getUser()),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }
}
