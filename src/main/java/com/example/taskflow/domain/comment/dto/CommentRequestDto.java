package com.example.taskflow.domain.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentRequestDto {

    @Size(min = 5,max = 30,message = "댓글은 5자이상 30자 이하로 입력해주세요.")
    private final String content;

    public CommentRequestDto(String content) {
        this.content = content;
    }
}
