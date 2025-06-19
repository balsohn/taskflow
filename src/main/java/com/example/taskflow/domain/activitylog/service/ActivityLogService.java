package com.example.taskflow.domain.activitylog.service;

import com.example.taskflow.domain.activitylog.dto.ActivityLogResponse;
import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.activitylog.mapper.ActivityLogMapper;
import com.example.taskflow.domain.activitylog.repository.ActivityLogRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.common.dto.PageResponse;
import com.example.taskflow.global.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    /**
     * 활동 로그 생성
     */
    @Transactional
    public void createActivityLog(
            Long userId,
            ActionType actionType,
            EntityType entityType,
            Long entityId,
            String description,
            String oldValue,
            String newValue,
            String ipAddress,
            String userAgent
    ) {
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 활동 로그 생성
        ActivityLog activityLog = new ActivityLog(
                user,
                actionType,
                entityType,
                entityId,
                description,
                oldValue,
                newValue,
                ipAddress,
                userAgent
        );

        // 저장
        activityLogRepository.save(activityLog);

        log.info("활동 로그 생성 완료 - 사용자: {}, 액션: {}, 엔티티: {}, 설명: {}",
                user.getName(), actionType, entityType, description);
    }

    /**
     * 전체  활동 로그 조회 (관리자용) - 필터링 지원
     */
    public PageResponse<ActivityLogResponse> getAllActivityLogs(
            ActionType actionType,
            Long entityId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page, int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ActivityLog> logs = activityLogRepository.findActivityLogsWithFilters(
                actionType, entityId, startDate, endDate, null, pageable);

        return PageResponse.of(logs, ActivityLogMapper::toResponse);
    }

    /**
     * 사용자별 활동 로그 - 필터링 지원
     */
    public PageResponse<ActivityLogResponse> getUserActivityLogs(
            Long userId,
            ActionType actionType,
            Long entityId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page, int size) {

        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ActivityLog> logs = activityLogRepository.findUserActivityLogsWithFilters(
                userId, actionType, entityId, startDate, endDate, pageable);

        return PageResponse.of(logs, ActivityLogMapper::toResponse);
    }

    /**
     * 기존 메서드들 (하위 호환성을 위해 유지)
     */
    public PageResponse<ActivityLogResponse> getAllActivityLogs(int page, int size) {
        return getAllActivityLogs(null, null, null, null, page, size);
    }

    public PageResponse<ActivityLogResponse> getUserActivityLogs(Long userId, int page, int size) {
        return getUserActivityLogs(userId, null, null, null, null, page, size);
    }

}
