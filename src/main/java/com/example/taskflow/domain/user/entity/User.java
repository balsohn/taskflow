package com.example.taskflow.domain.user.entity;

import com.example.taskflow.domain.user.enums.UserRoleEnum;
import com.example.taskflow.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    @Column(unique = true)
    private String username;

    @NotEmpty
    private String name;

    @NotEmpty
    private String password;

    @Email
    @Column(unique = true)
    private String email;

    @Column
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(String username, String password, String email, String name, UserRoleEnum role) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}

