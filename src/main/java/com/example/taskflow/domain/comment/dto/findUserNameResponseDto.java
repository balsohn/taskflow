package com.example.taskflow.domain.comment.dto;

import com.example.taskflow.domain.comment.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class findUserNameResponseDto {

    @NotBlank
    private final String userName;

    @NotBlank
    private final String content;

    public findUserNameResponseDto(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public static findUserNameResponseDto findUserNameDto(Comment comment){
        return findUserNameResponseDto.builder()
                .userName(comment.getUser().getName())
                .content(comment.getContent())
                .build();
    }
}
