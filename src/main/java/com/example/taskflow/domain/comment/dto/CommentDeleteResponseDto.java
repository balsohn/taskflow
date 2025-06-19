package com.example.taskflow.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDeleteResponseDto {

    private Boolean isDeleted = false;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private final LocalDateTime deletedAt;

    public CommentDeleteResponseDto(Boolean isDeleted,
                                    LocalDateTime deletedAt) {
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }
}
