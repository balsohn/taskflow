package com.example.taskflow.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private final Long userId;
    private final int taskId;
    @NotBlank
    private final String comment;
    @NotBlank
    //private final String userName;
    private final Boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime modifiedAt;

    public CommentResponseDto(Long userId ,int taskId,String comment,Boolean isDeleted, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.userId = userId;
        this.taskId = taskId;
        this.comment = comment;
        //this.userName = userName;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
