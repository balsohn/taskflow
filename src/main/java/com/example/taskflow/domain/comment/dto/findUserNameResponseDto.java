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
    private final String comment;

    public findUserNameResponseDto(String userName, String comment) {
        this.userName = userName;
        this.comment = comment;
    }

    public static findUserNameResponseDto findUserNameDto(Comment comment){
        return findUserNameResponseDto.builder()
                .userName(comment.getUser().getName())
                .comment(comment.getComment())
                .build();
    }
}
