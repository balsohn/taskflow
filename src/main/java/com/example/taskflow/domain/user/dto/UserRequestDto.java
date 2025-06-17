package com.example.taskflow.domain.user.dto;

import com.example.taskflow.domain.user.enums.UserRoleEnum;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserRequestDto {
    private final String username;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다."
    )
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