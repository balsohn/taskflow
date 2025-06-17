package com.example.taskflow.domain.activitylog.controller;

import com.example.taskflow.domain.activitylog.dto.ActivityLogResponse;
import com.example.taskflow.domain.activitylog.service.ActivityLogService;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    /**
     * 전체 활동 로그 조회 (관리자용)
     * GET /api/activity-logs?page=0&size=10
     */
    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ActivityLogResponse>>> getAllActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponse<ActivityLogResponse> response = activityLogService.getAllActivityLogs(page, size);

        return ResponseEntity.ok(ApiResponse.success("전체 활동 로그 조회 성공", response));
    }

    /**
     * 특정 사용자 활동 로그 조회 (관리자 전용)
     * GET /api/activity-logs/user/{userId}
     */
    @GetMapping("/user/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ActivityLogResponse>>> getUserActivityLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponse<ActivityLogResponse> response = activityLogService.getUserActivityLogs(userId, page, size);

        return ResponseEntity.ok(ApiResponse.success("사용자 활동 로그 조회 성공", response));
    }

    /**
     * 내 활동 로그 조회 (일반 사용자용)
     * GET /api/activity-logs/my?page=0&size=10
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<ActivityLogResponse>>> getMyActivityLogs(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        // TODO: 현재 로그인한 사용자 ID 추출
        // 임시로 하드코딩
        Long currentUserId = 1L;

        PageResponse<ActivityLogResponse> response = activityLogService.getUserActivityLogs(currentUserId, page, size);

        return ResponseEntity.ok(ApiResponse.success("내 활동 로그 조회 성공", response));
    }
}
