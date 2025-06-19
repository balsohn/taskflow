package com.example.taskflow.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class findUserNameRequestDto {

    @NotBlank
    private final String userName;

    public findUserNameRequestDto(String userName) {
        this.userName = userName;
    }
}