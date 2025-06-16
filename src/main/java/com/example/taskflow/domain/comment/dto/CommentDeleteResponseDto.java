package com.example.taskflow.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDeleteResponseDto {

    private final Boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public CommentDeleteResponseDto(Boolean isDeleted, LocalDateTime createdAt) {
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }
}
