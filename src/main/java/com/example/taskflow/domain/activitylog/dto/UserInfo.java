package com.example.taskflow.domain.activitylog.dto;

import com.example.taskflow.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserInfo {
    private final String name;
    private final String username;
    private final String email;

    public UserInfo(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public static UserInfo fromUser(User user) {
        return new UserInfo(user.getName(), user.getUsername(), user.getEmail());
    }
}
