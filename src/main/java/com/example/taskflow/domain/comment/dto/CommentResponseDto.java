package com.example.taskflow.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    @Size(min = 5,max = 30,message = "댓글은 5자이상 30자 이하로 입력해주세요.")
    @NotBlank
    private final String detail;
    @NotBlank
    private final String userName;
    private final Long taskId;
    private final Boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime modifiedAt;

    public CommentResponseDto(String detail, String userName, Long taskId, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.detail = detail;
        this.userName = userName;
        this.taskId = taskId;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
