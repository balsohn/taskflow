package com.example.taskflow.domain.activitylog.entity;

public enum EntityType {
    TASK("태스크"),
    COMMENT("댓글"),
    USER("사용자");

    private final String description;

    EntityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
