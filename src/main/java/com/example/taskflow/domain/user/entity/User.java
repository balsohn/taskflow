package com.example.taskflow.domain.user.entity;

import com.example.taskflow.domain.user.enums.UserRoleEnum;
import com.example.taskflow.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Table(name = "users")
@Getter
@Setter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인할때 쓸 아이디
    @NotBlank
    private String username;

    @NotEmpty
    private String name;

    @NotEmpty
    private String password;

    @Email
    @Column(unique = true)
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "유효한 이메일 형식이 아닙니다."
    )
    private String email;

    @Column
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(String username, String name, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
    }
}

