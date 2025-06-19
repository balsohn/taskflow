package com.example.taskflow.domain.user.entity;

import com.example.taskflow.domain.user.enums.UserRoleEnum;
import com.example.taskflow.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Size(min =4,max = 20,
    message = "아이디는 4-20자 그리고 영어나 숫자만 가능합니다.")
    private String username;

    @NotEmpty
    @Size(min = 2,max = 50,
    message = "이름은 2-50자 내외로 설정해주세요")
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

