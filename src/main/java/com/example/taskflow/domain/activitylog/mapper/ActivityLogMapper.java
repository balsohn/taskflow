package com.example.taskflow.domain.activitylog.mapper;

import com.example.taskflow.domain.activitylog.dto.ActivityLogResponse;
import com.example.taskflow.domain.activitylog.entity.ActivityLog;

public class ActivityLogMapper {

    /**
     * ActivityLog 엔티티를 응답 DTO로 변환
     */
    public static ActivityLogResponse toResponse(ActivityLog activityLog) {
        return new ActivityLogResponse(
                activityLog.getId(),
                activityLog.getCreatedAt(),
                getUserDisplayName(activityLog),
                getActionTypeDisplay(activityLog.getActionType().name()),
                activityLog.getEntityType().name(),
                activityLog.getEntityId(),
                activityLog.getDescription()
        );
    }

    /**
     * 사용자 표시명 결정
     */
    private static String getUserDisplayName(ActivityLog activityLog) {
        if (activityLog.getUser() == null) {
            return "알 수 없는 사용자";
        }

        // name이 있으면 name 사용, 없으면 username 사용
        String name = activityLog.getUser().getName();
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }

        String username = activityLog.getUser().getUsername();
        if (username != null && !username.trim().isEmpty()) {
            return username;
        }

        return "알 수 없는 사용자";
    }

    /**
     * 액션 타입 한글 표시명
     */
    private static String getActionTypeDisplay(String actionType) {
        return switch (actionType) {
            case "CREATE" -> "생성";
            case "UPDATE" -> "수정";
            case "DELETE" -> "삭제";
            case "STATUS_CHANGE" -> "상태 변경";
            case "LOGIN" -> "로그인";
            default -> actionType;
        };
    }

    /**
     * 엔티티 타입 한글 표시명
     */
    private static String getEntityTypeDisplay(String entityType) {
        return switch (entityType) {
            case "TASK" -> "작업";
            case "COMMENT" -> "댓글";
            case "USER" -> "사용자";
            default -> entityType;
        };
    }
}