package com.example.taskflow.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    @NotBlank
    private final String comment;
    @NotBlank
    private final String userName;
    //private final Long taskId;
    private final int taskId;
    private final Boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime modifiedAt;

    public CommentResponseDto(String comment, String userName, int taskId, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.comment = comment;
        this.userName = userName;
        //this.taskId = taskId;
        this.taskId = taskId;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
