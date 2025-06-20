package com.example.taskflow.domain.activitylog.mapper;

import com.example.taskflow.domain.activitylog.dto.ActivityLogResponse;
import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.activitylog.dto.UserInfo;

public class ActivityLogMapper {

    /**
     * ActivityLog 엔티티를 응답 DTO로 변환
     */
    public static ActivityLogResponse toResponse(ActivityLog activityLog) {
        return new ActivityLogResponse(
                activityLog.getId(),
                generateActivityType(activityLog.getActionType(), activityLog.getEntityType()),
                activityLog.getUser().getId(),
                UserInfo.fromUser(activityLog.getUser()),
                getTaskId(activityLog),
                activityLog.getCreatedAt(),
                activityLog.getDescription()
        );
    }

    /**
     * ActionType과 EntityType을 조합해서 프론트엔드에서 사용하는 type 생성
     * 예: CREATE + TASK = "TASK_CREATED"
     */
    private static String generateActivityType(ActionType actionType, EntityType entityType) {
        String entity = entityType.name(); // TASK, COMMENT, USER

        return switch (actionType) {
            case CREATE -> entity + "_CREATED";
            case UPDATE -> entity + "_UPDATED";
            case DELETE -> entity + "_DELETED";
            case STATUS_CHANGE -> "TASK_STATUS_CHANGED";  // Task 상태 변경은 고정
            case LOGIN -> "USER_LOGIN";  // 로그인은 고정
            default -> entity + "_" + actionType.name();
        };
    }

    /**
     * Task 관련 로그인 경우에만 taskId 반환, 그 외에는 null
     */
    private static Long getTaskId(ActivityLog activityLog) {
        if (activityLog.getEntityType() == EntityType.TASK) {
            return activityLog.getEntityId();
        }
        return null;
    }

//    /**
//     * 사용자 표시명 결정
//     */
//    private static String getUserDisplayName(ActivityLog activityLog) {
//        if (activityLog.getUser() == null) {
//            return "알 수 없는 사용자";
//        }
//
//        // name이 있으면 name 사용, 없으면 username 사용
//        String name = activityLog.getUser().getName();
//        if (name != null && !name.trim().isEmpty()) {
//            return name;
//        }
//
//        String username = activityLog.getUser().getUsername();
//        if (username != null && !username.trim().isEmpty()) {
//            return username;
//        }
//
//        return "알 수 없는 사용자";
//    }
//
//    /**
//     * 액션 타입 한글 표시명
//     */
//    private static String getActionTypeDisplay(String actionType) {
//        return switch (actionType) {
//            case "CREATE" -> "생성";
//            case "UPDATE" -> "수정";
//            case "DELETE" -> "삭제";
//            case "STATUS_CHANGE" -> "상태 변경";
//            case "LOGIN" -> "로그인";
//            default -> actionType;
//        };
//    }
//
//    /**
//     * 엔티티 타입 한글 표시명
//     */
//    private static String getEntityTypeDisplay(String entityType) {
//        return switch (entityType) {
//            case "TASK" -> "작업";
//            case "COMMENT" -> "댓글";
//            case "USER" -> "사용자";
//            default -> entityType;
//        };
//    }
}