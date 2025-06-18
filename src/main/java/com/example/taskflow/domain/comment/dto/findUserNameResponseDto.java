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
    private final String detail;

    public findUserNameResponseDto(String userName, String detail) {
        this.userName = userName;
        this.detail = detail;
    }

    public static findUserNameResponseDto findUserNameDto(Comment comment){
        return findUserNameResponseDto.builder()
                .userName(comment.getUser().getName())
                .detail(comment.getDetail())
                .build();
    }
}
