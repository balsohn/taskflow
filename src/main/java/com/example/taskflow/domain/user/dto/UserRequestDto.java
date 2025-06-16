package com.example.taskflow.domain.user.dto;

import com.example.taskflow.domain.user.enums.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserRequestDto {
    private final String username;
    private final String password;
    private final String email;
    private final String name;
    private final UserRoleEnum role;

    public UserRequestDto(String name, String password, String email, String username, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}