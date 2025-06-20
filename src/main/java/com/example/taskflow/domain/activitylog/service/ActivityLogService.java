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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

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

        activityLogRepository.save(activityLog);

        log.info("활동 로그 생성 완료 - 사용자: {}, 액션: {}, 엔티티: {}, 설명: {}",
                user.getName(), actionType, entityType, description);
    }

    /**
     * 전체 활동 로그 조회 (메인 메서드)
     */
    public PageResponse<ActivityLogResponse> getAllActivityLogs(
            ActionType actionType,
            String entityTypeStr,
            Long taskId,
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 문자열을 EntityType으로 변환
        EntityType entityType = convertStringToEntityType(entityTypeStr);

        // taskId가 있으면 Task 관련 로그만 조회하도록 설정
        Long entityId = null;
        if (taskId != null) {
            entityType = EntityType.TASK;
            entityId = taskId;
        }

        // fetch join으로 N+1 문제 해결된 메서드 사용
        Page<ActivityLog> logs = activityLogRepository.findActivityLogsWithFilters(
                actionType, entityType, entityId, startDate, endDate, userId, pageable);

        return PageResponse.of(logs, ActivityLogMapper::toResponse);
    }

    /**
     * 사용자별 활동 로그 조회
     */
    public PageResponse<ActivityLogResponse> getUserActivityLogs(
            Long userId,
            ActionType actionType,
            String entityTypeStr,
            Long taskId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        // getAllActivityLogs 메서드 재사용 (userId 필터링 포함)
        return getAllActivityLogs(actionType, entityTypeStr, taskId, userId, startDate, endDate, page, size);
    }

    /**
     * 문자열을 EntityType으로 변환하는 헬퍼 메서드
     */
    private EntityType convertStringToEntityType(String entityTypeStr) {
        if (entityTypeStr == null || entityTypeStr.trim().isEmpty()) {
            return null;
        }

        try {
            return EntityType.valueOf(entityTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 EntityType 문자열: {}", entityTypeStr);
            return null;
        }
    }

    // ==================== 하위 호환성을 위한 오버로드 메서드들 ====================

    /**
     * 전체 활동 로그 조회 (단순 버전)
     */
    public PageResponse<ActivityLogResponse> getAllActivityLogs(int page, int size) {
        return getAllActivityLogs(null, null, null, null, null, null, page, size);
    }

    /**
     * 사용자별 활동 로그 조회 (단순 버전)
     */
    public PageResponse<ActivityLogResponse> getUserActivityLogs(Long userId, int page, int size) {
        return getUserActivityLogs(userId, null, null, null, null, null, page, size);
    }

    /**
     * 액션 타입과 엔티티 ID로 조회 (레거시 호환)
     */
    public PageResponse<ActivityLogResponse> getAllActivityLogs(
            ActionType actionType,
            Long entityId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {
        return getAllActivityLogs(actionType, null, entityId, null, startDate, endDate, page, size);
    }

    /**
     * 사용자별 + 액션 타입으로 조회 (레거시 호환)
     */
    public PageResponse<ActivityLogResponse> getUserActivityLogs(
            Long userId,
            ActionType actionType,
            Long entityId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {
        return getUserActivityLogs(userId, actionType, null, entityId, startDate, endDate, page, size);
    }
}