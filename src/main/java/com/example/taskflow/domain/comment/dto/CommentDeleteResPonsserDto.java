package com.example.taskflow.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDeleteResPonsserDto {

    private Boolean isDeleted = false;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime deletedAt;

    public CommentDeleteResPonsserDto(Boolean isDeleted,
                                      LocalDateTime deletedAt) {
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }
}
