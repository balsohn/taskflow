package com.example.taskflow.domain.task.dto.response;

public class UserInfo {

    private final Long id;
    private final String name;
    private final String email;

    public UserInfo(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
