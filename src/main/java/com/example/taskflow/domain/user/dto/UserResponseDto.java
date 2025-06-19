package com.example.taskflow.domain.user.dto;

import com.example.taskflow.domain.user.enums.UserRoleEnum;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String name;
    private final String email;
    private final UserRoleEnum role;
    private final String username;
    private final LocalDateTime createdAt;


    public UserResponseDto(Long id, String username,String email, String name, UserRoleEnum role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.name = name;
        this.createdAt = createdAt;
    }


}