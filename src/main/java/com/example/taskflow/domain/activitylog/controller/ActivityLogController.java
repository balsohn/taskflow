package com.example.taskflow.domain.activitylog.controller;

import com.example.taskflow.domain.activitylog.dto.ActivityLogResponse;
import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.service.ActivityLogService;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.enums.UserRoleEnum;
import com.example.taskflow.global.aop.ActivityLogHelper;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.dto.PageResponse;
import com.example.taskflow.global.exception.custom.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final ActivityLogHelper activityLogHelper;

    /**
     * 통합 활동 로그 조회 API
     * - 일반 사용자: GET /api/activity-logs?page=0&size=10 (본인 로그만)
     * - 관리자 (전체): GET /api/activity-logs?page=0&size=10 (전체 로그)
     * - 관리자 (특정 사용자): GET /api/activity-logs?userId=123&page=0&size=10
     * - 필터링: GET /api/activity-logs?type=TASK_CREATED&taskId=1&startDate=2025-06-01T00:00:00
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ActivityLogResponse>>> getActivityLogs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 1. 현재 로그인 사용자 확인
        Long currentUserId = activityLogHelper.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedActionException("인증되지 않은 사용자입니다.");
        }

        User currentUser = activityLogHelper.getCurrentUser();
        boolean isAdmin = currentUser.getRole() == UserRoleEnum.ADMIN;

        // 2. type을 ActionType과 EntityType으로 변환
        ActivityLogQueryParams params = parseActivityType(type);

        // 3. 권한에 따른 로직 분기
        PageResponse<ActivityLogResponse> response;

        if (isAdmin) {
            // 관리자
            if (userId != null) {
                // 특정사용자 로그 조회
                response = activityLogService.getUserActivityLogs(
                        userId, params.actionType(), params.entityType(), taskId, startDate, endDate, page, size
                );
            } else {
                // 전체로그 조회
                response = activityLogService.getAllActivityLogs(
                        params.actionType(), params.entityType(), taskId, userId, startDate, endDate, page, size
                );
            }
        } else {
            // 일반사용자
            if (userId != null && !userId.equals(currentUserId)) {
                // 다른 사용자 로그 조회 차단
                throw new UnauthorizedActionException("다른 사용자의 활동 로그를 조회할 권한이 없습니다.");
            }

            response = activityLogService.getUserActivityLogs(
                    currentUserId, params.actionType(), params.entityType(), taskId, startDate, endDate, page, size
            );
        }

        return ResponseEntity.ok(ApiResponse.success("활동 로그 조회 성공", response));
    }

    /**
     * 프론트엔드 type을 백엔드 ActionType과 EntityType으로 파싱
     * 예: TASK_CREATED -> ActionType.CREATE, EntityType.TASK
     */
    private ActivityLogQueryParams parseActivityType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return new ActivityLogQueryParams(null, null);
        }

        return switch (type.toUpperCase()) {
            case "TASK_CREATED" -> new ActivityLogQueryParams(ActionType.CREATE, "TASK");
            case "TASK_UPDATED" -> new ActivityLogQueryParams(ActionType.UPDATE, "TASK");
            case "TASK_DELETED" -> new ActivityLogQueryParams(ActionType.DELETE, "TASK");
            case "TASK_STATUS_CHANGED" -> new ActivityLogQueryParams(ActionType.STATUS_CHANGE, "TASK");
            case "COMMENT_CREATED" -> new ActivityLogQueryParams(ActionType.CREATE, "COMMENT");
            case "COMMENT_UPDATED" -> new ActivityLogQueryParams(ActionType.UPDATE, "COMMENT");
            case "COMMENT_DELETED" -> new ActivityLogQueryParams(ActionType.DELETE, "COMMENT");
            case "USER_LOGIN" -> new ActivityLogQueryParams(ActionType.LOGIN, "USER");  // LOGIN으로 수정
            case "USER_CREATED" -> new ActivityLogQueryParams(ActionType.CREATE, "USER");
            case "USER_DELETED" -> new ActivityLogQueryParams(ActionType.DELETE, "USER");
            default -> new ActivityLogQueryParams(null, null);
        };
    }

    /**
     * 쿼리 파라미터 레코드
     */
    private record ActivityLogQueryParams(ActionType actionType, String entityType) {}
}