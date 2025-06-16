package com.example.taskflow.domain.activitylog.service;

import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.activitylog.repository.ActivityLogRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
