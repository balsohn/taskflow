package com.example.taskflow.domain.user.dto;

import lombok.Getter;

@Getter
public class UserRequestDto {
    private final String name;
    private final String password;
    private final String email;
    private final String username;

    public UserRequestDto(String name, String password, String email, String username) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.username = username;
    }
}