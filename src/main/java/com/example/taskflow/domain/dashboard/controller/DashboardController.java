package com.example.taskflow.domain.dashboard.controller;

import com.example.taskflow.domain.dashboard.dto.DashboardResponse;
import com.example.taskflow.domain.dashboard.service.DashboardService;
import com.example.taskflow.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardResponse>> stats(
            @AuthenticationPrincipal User user
            ) {

        DashboardResponse stats = dashboardService.stats(user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("대시보드 조회에 성공하였습니다", stats));
    }
}
