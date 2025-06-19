package com.example.taskflow.domain.user.dto;

import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.enums.UserRoleEnum;
import lombok.Getter;
//"id": 1,
//            "email": "test@test.com",
//            "name": "테스트 사용자",
//            "role": "USER"

@Getter
public class FindUsernameResponseDto {
    private final Long id;
    private final String email;
    private final String name;
    private final UserRoleEnum role;

    public FindUsernameResponseDto(Long id, String email, String name, UserRoleEnum role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public static FindUsernameResponseDto toDto(User user){
        return new FindUsernameResponseDto(user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole());
    }
}
