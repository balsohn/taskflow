package com.example.taskflow.domain.activitylog.controller;

import com.example.taskflow.domain.activitylog.dto.ActivityLogResponse;
import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.service.ActivityLogService;
import com.example.taskflow.global.aop.ActivityLogHelper;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.dto.PageResponse;
import com.example.taskflow.global.exception.custom.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final ActivityLogHelper activityLogHelper;

    /**
     * 전체 활동 로그 조회 (관리자용)
     * GET /api/activity-logs?page=0&size=10
     */
    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ActivityLogResponse>>> getAllActivityLogs(
            @RequestParam(required = false) ActionType actionType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponse<ActivityLogResponse> response = activityLogService.getAllActivityLogs(
                actionType, entityId, startDate, endDate, page, size
        );

        return ResponseEntity.ok(ApiResponse.success("전체 활동 로그 조회 성공", response));
    }

    /**
     * 특정 사용자 활동 로그 조회 (관리자 전용)
     * GET /api/activity-logs/user/{userId}?actionType=UPDATE&entityId=1&page=0&size=10
     */
    @GetMapping("/user/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ActivityLogResponse>>> getUserActivityLogs(
            @PathVariable Long userId,
            @RequestParam(required = false) ActionType actionType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponse<ActivityLogResponse> response = activityLogService.getUserActivityLogs(
                userId, actionType, entityId, startDate, endDate, page, size);

        return ResponseEntity.ok(ApiResponse.success("사용자 활동 로그 조회 성공", response));
    }

    /**
     * 내 활동 로그 조회 (일반 사용자용)
     * GET /api/activity-logs/my?actionType=CREATE&entityId=1&page=0&size=10
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<ActivityLogResponse>>> getMyActivityLogs(
            @RequestParam(required = false) ActionType actionType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Long currentUserId = activityLogHelper.getCurrentUserId();

        if (currentUserId == null) {
            throw new UnauthorizedActionException("인증되지 않은 사용자입니다.");
        }

        PageResponse<ActivityLogResponse> response = activityLogService.getUserActivityLogs(
                currentUserId, actionType, entityId, startDate, endDate, page, size);

        return ResponseEntity.ok(ApiResponse.success("내 활동 로그 조회 성공", response));
    }
}
