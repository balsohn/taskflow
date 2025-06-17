package com.example.taskflow.global.aspect;

import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.activitylog.service.ActivityLogService;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * AOP를 활용한 활동 로그 자동 기록 시스템
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLogAspect {

    private final ActivityLogService activityLogService;
    private final UserRepository userRepository;

    /**
     * Task 관련 메서드들에 대한 Pointcut 정의
     */
    @Pointcut("execution(* com.example.taskflow.domain.task.service.*.*(..))")
    public void taskServiceMethods() {}

    /**
     * User 관련 메서드들에 대한 Pointcut 정의
     */
    @Pointcut("execution(* com.example.taskflow.domain.user.service.*.*(..))")
    public void userServiceMethods() {}

    /**
     * Comment 관련 메서드들에 대한 Pointcut 정의
     */
    @Pointcut("execution(* com.example.taskflow.domain.comment.service.*.*(..))")
    public void commentServiceMethods() {}

    // ==================== TASK 관련 AOP 로직 ====================

    /**
     * Task 생성 후 활동 로그 기록
     */
    @AfterReturning(value = "taskServiceMethods() && execution(* *..createTask(..))",
            returning = "result")
    public void logTaskCreation(JoinPoint joinPoint, Object result) {
        try {
            Long taskId = extractIdFromResult(result);
            String taskTitle = extractTitleFromResult(result);

            logActivity(
                    ActionType.CREATE,
                    EntityType.TASK,
                    taskId,
                    "새로운 태스크 '" + taskTitle + "'을(를) 생성했습니다.",
                    null,
                    taskTitle
            );

            log.info("Task 생성 로그 기록 완료 - ID: {}, 제목: {}", taskId, taskTitle);
        } catch (Exception e) {
            log.error("Task 생성 로그 기록 중 오류 발생", e);
        }
    }

    /**
     * Task 수정 후 활동 로그 기록
     */
    @AfterReturning(value = "taskServiceMethods() && execution(* *..updateTask(..))",
            returning = "result")
    public void logTaskUpdate(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            Long taskId = extractTaskIdFromArgs(args);
            String newTitle = extractTitleFromResult(result);

            logActivity(
                    ActionType.UPDATE,
                    EntityType.TASK,
                    taskId,
                    "태스크 정보를 수정했습니다.",
                    null, // 이전 값은 별도 조회 필요
                    newTitle
            );

            log.info("Task 수정 로그 기록 완료 - ID: {}", taskId);
        } catch (Exception e) {
            log.error("Task 수정 로그 기록 중 오류 발생", e);
        }
    }

    /**
     * Task 상태 변경 후 활동 로그 기록
     */
    @AfterReturning(value = "taskServiceMethods() && execution(* *..updateTaskStatus(..))",
            returning = "result")
    public void logTaskStatusChange(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            Long taskId = extractTaskIdFromArgs(args);
            String newStatus = extractStatusFromArgs(args);

            logActivity(
                    ActionType.STATUS_CHANGE,
                    EntityType.TASK,
                    taskId,
                    "태스크 상태를 " + newStatus + "로 변경했습니다.",
                    null, // 이전 상태는 별도 조회 필요
                    newStatus
            );

            log.info("Task 상태 변경 로그 기록 완료 - ID: {}, 새 상태: {}", taskId, newStatus);
        } catch (Exception e) {
            log.error("Task 상태 변경 로그 기록 중 오류 발생", e);
        }
    }

    /**
     * Task 삭제 후 활동 로그 기록
     */
    @AfterReturning(value = "taskServiceMethods() && execution(* *..deleteTask(..))")
    public void logTaskDeletion(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Long taskId = extractTaskIdFromArgs(args);

            logActivity(
                    ActionType.DELETE,
                    EntityType.TASK,
                    taskId,
                    "태스크를 삭제했습니다.",
                    null,
                    null
            );

            log.info("Task 삭제 로그 기록 완료 - ID: {}", taskId);
        } catch (Exception e) {
            log.error("Task 삭제 로그 기록 중 오류 발생", e);
        }
    }

    // ==================== 공통 메서드들 ====================

    /**
     * 공통 활동 로그 기록 메서드
     */
    private void logActivity(ActionType actionType, EntityType entityType, Long entityId,
                             String description, String oldValue, String newValue) {
        try {
            // 현재 인증된 사용자 정보 가져오기
            Long userId = getCurrentUserId();

            if (userId == null) {
                log.warn("사용자 ID를 찾을 수 없어 활동 로그를 기록하지 않습니다. 설명: {}", description);
                return;
            }

            // HTTP 요청 정보 가져오기
            String ipAddress = getClientIpAddress();
            String userAgent = getUserAgent();

            // 활동 로그 생성
            activityLogService.createActivityLog(
                    userId,
                    actionType,
                    entityType,
                    entityId,
                    description,
                    oldValue,
                    newValue,
                    ipAddress,
                    userAgent
            );

            log.info("활동 로그 기록 완료 - 사용자: {}, 액션: {}, 엔티티: {}, 설명: {}",
                    userId, actionType, entityType, description);

        } catch (Exception e) {
            log.error("활동 로그 기록 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 현재 인증된 사용자 ID 가져오기 (완전 구현)
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {

                String username = authentication.getName();

                if (username != null && !username.isEmpty()) {
                    // 사용자명으로 실제 User 엔티티 조회
                    Optional<User> userOpt = userRepository.findByUsername(username);

                    if (userOpt.isPresent()) {
                        Long userId = userOpt.get().getId();
                        log.debug("현재 사용자 ID: {} (사용자명: {})", userId, username);
                        return userId;
                    } else {
                        log.warn("사용자를 찾을 수 없습니다. 사용자명: {}", username);
                    }
                } else {
                    log.warn("사용자명이 비어있습니다.");
                }
            } else {
                log.debug("인증되지 않은 사용자 또는 익명 사용자입니다.");
            }
        } catch (Exception e) {
            log.error("사용자 ID 추출 중 오류 발생: {}", e.getMessage(), e);
        }

        return null;
    }

    /**
     * 클라이언트 IP 주소 가져오기
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            return request.getRemoteAddr();
        } catch (Exception e) {
            log.warn("IP 주소 추출 중 오류 발생", e);
            return "Unknown";
        }
    }

    /**
     * 사용자 에이전트 정보 가져오기
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("User-Agent");
        } catch (Exception e) {
            log.warn("User-Agent 추출 중 오류 발생", e);
            return "Unknown";
        }
    }

    // ==================== 데이터 추출 유틸리티 메서드들 ====================

    /**
     * 결과 객체에서 ID 추출 (기본 구현)
     */
    private Long extractIdFromResult(Object result) {
        try {
            if (result == null) return null;

            // Reflection을 사용하여 id 필드 추출
            Field idField = result.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object value = idField.get(result);
            return value instanceof Long ? (Long) value : null;
        } catch (Exception e) {
            log.warn("결과에서 ID 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 결과 객체에서 제목 추출 (기본 구현)
     */
    private String extractTitleFromResult(Object result) {
        try {
            if (result == null) return null;

            Field titleField = result.getClass().getDeclaredField("title");
            titleField.setAccessible(true);
            Object value = titleField.get(result);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.warn("결과에서 제목 추출 실패: {}", e.getMessage());
            return "제목 없음";
        }
    }

    /**
     * 메서드 인자에서 Task ID 추출
     */
    private Long extractTaskIdFromArgs(Object[] args) {
        try {
            // 첫 번째 인자가 보통 ID인 경우가 많음
            if (args.length > 0 && args[0] instanceof Long) {
                return (Long) args[0];
            }
        } catch (Exception e) {
            log.warn("인자에서 Task ID 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 메서드 인자에서 상태 정보 추출
     */
    private String extractStatusFromArgs(Object[] args) {
        try {
            // 상태 변경 메서드에서 두 번째 인자가 보통 새로운 상태
            if (args.length > 1 && args[1] != null) {
                return args[1].toString();
            }
        } catch (Exception e) {
            log.warn("인자에서 상태 추출 실패: {}", e.getMessage());
        }
        return "알 수 없음";
    }

}