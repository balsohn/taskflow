package com.example.taskflow.domain.user.dto;

import com.example.taskflow.domain.user.enums.UserRoleEnum;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserRegisterResponseDto {
    private final Long id;
    private final String name;
    private final String email;
    private final UserRoleEnum role;
    private final String username;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public UserRegisterResponseDto(Long id, String name, String email, UserRoleEnum role, String username, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.username = username;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
