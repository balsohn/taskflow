package com.example.taskflow.domain.activitylog.entity;

/**
 * 활동 로그의 액션 타입을 정의하는 Enum
 */
public enum ActionType {
    LOGIN("로그인"),
    CREATE("생성"),
    UPDATE("수정"),
    DELETE("삭제"),
    STATUS_CHANGE("상태 변경");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
