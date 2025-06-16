package com.example.taskflow.domain.task.dto.response;

import com.example.taskflow.domain.user.entity.User;

public class UserInfo {

    private final Long id;
    private final String name;
    private final String email;

    public UserInfo(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static UserInfo fromUser(User user) {
        return new UserInfo(user.getId(), user.getName(), user.getEmail());
    }
}
