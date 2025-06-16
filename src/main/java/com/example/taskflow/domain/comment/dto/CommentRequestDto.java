package com.example.taskflow.domain.comment.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {

    private final String detail;

    public CommentRequestDto(String detail) {
        this.detail = detail;
    }
}
