package com.example.taskflow.domain.user.entity;

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
@Table(name = "user")
@Getter
@Setter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

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
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "유효한 이메일 형식이 아닙니다."
    )
    private String email;

    @Column
    private String role;
}

