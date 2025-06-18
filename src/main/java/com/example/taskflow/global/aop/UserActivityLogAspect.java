package com.example.taskflow.global.aop;

import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User 도메인 전용 활동 로그 Aspect
 * - 회원가입, 로그인, 회원정보 수정, 회원탈퇴 로깅
 * - 안전한 ThreadLocal 관리
 * - 향상된 예외 처리
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityLogAspect {

    private final ActivityLogHelper activityLogHelper;
    private final UserRepository userRepository;

    // 변경 전 값을 임시 저장하는 ThreadLocal Map
    private final ThreadLocal<Map<String, Object>> userContext =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    // ==================== Pointcut 정의 ====================

    @Pointcut("execution(* com.example.taskflow.domain.user.service.*.*(..))")
    public void userServiceMethods() {}

    @Pointcut("userServiceMethods() && execution(* *..createUser(..))")
    public void userCreationMethods() {}

    @Pointcut("userServiceMethods() && execution(* *..login(..))")
    public void userLoginMethods() {}

    @Pointcut("userServiceMethods() && execution(* *..update*(..))")
    public void userUpdateMethods() {}

    @Pointcut("userServiceMethods() && execution(* *..deleteUser(..))")
    public void userDeletionMethods() {}

    // ==================== 회원가입 ====================

    @AfterReturning(value = "userCreationMethods()", returning = "result")
    public void logUserCreation(JoinPoint joinPoint, Object result) {
        try {
            // result에서 사용자 정보 추출
            Long userId = activityLogHelper.extractIdFromResult(result);
            String username = extractUsernameFromResult(result);
            String email = extractEmailFromResult(result);

            log.debug("회원가입 로그 기록 시도 - ID: {}, 사용자명: {}", userId, username);

            if (userId != null && username != null) {
                String description = String.format("새로운 사용자 '%s'가 회원가입했습니다.", username);

                // 회원가입은 사용자 ID로 직접 로그 기록
                activityLogHelper.logActivityWithUserId(
                        userId,
                        ActionType.CREATE,
                        EntityType.USER,
                        userId,
                        description,
                        null,
                        username
                );

                log.debug("User 회원가입 로그 기록 완료 - ID: {}, 사용자명: {}", userId, username);
            } else {
                log.warn("회원가입 로그 기록 실패 - ID: {}, 사용자명: {}", userId, username);
            }
        } catch (Exception e) {
            log.error("User 회원가입 로그 기록 중 오류 발생", e);
        }
    }

    // ==================== 로그인 ====================

    @AfterReturning(value = "userLoginMethods()", returning = "result")
    public void logUserLogin(JoinPoint joinPoint, Object result) {
        try {
            // 로그인 성공 시에만 로그 기록 (result가 null이 아니면 성공으로 간주)
            if (result != null) {
                Object[] args = joinPoint.getArgs();
                String username = extractUsernameFromLoginArgs(args);

                if (username != null) {
                    // 사용자 정보 조회해서 ID 추출
                    Optional<User> userOpt = userRepository.findByUsername(username);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();

                        String description = String.format("사용자 '%s'가 로그인했습니다.", username);

                        activityLogHelper.logActivityWithUser(
                                user,
                                ActionType.LOGIN,
                                EntityType.USER,
                                user.getId(),
                                description,
                                null,
                                username
                        );

                        log.debug("User 로그인 로그 기록 완료 - 사용자명: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("User 로그인 로그 기록 중 오류 발생", e);
        }
    }

    // ==================== 회원정보 수정 ====================

    @Before("userUpdateMethods()")
    public void beforeUserUpdate(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Long userId = extractUserIdFromArgs(args);

            if (userId != null) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User oldUser = userOpt.get();

                    Map<String, Object> context = userContext.get();
                    context.put("userId", userId);
                    context.put("oldUsername", oldUser.getUsername());
                    context.put("oldEmail", oldUser.getEmail());
                    context.put("oldName", oldUser.getName());

                    log.debug("User 수정 전 값 저장 완료 - ID: {}", userId);
                }
            }
        } catch (Exception e) {
            log.debug("User 수정 전 값 저장 실패 (계속 진행): {}", e.getMessage());
        }
    }

    @AfterReturning(value = "userUpdateMethods()", returning = "result")
    public void logUserUpdate(JoinPoint joinPoint, Object result) {
        try {
            Map<String, Object> context = userContext.get();
            Long userId = (Long) context.get("userId");

            if (userId == null) {
                // context에 없으면 result에서 추출 시도
                userId = activityLogHelper.extractIdFromResult(result);
            }

            if (userId != null) {
                String oldUsername = (String) context.get("oldUsername");
                String oldEmail = (String) context.get("oldEmail");
                String oldName = (String) context.get("oldName");

                String newUsername = extractUsernameFromResult(result);
                String newEmail = extractEmailFromResult(result);
                String newName = extractNameFromResult(result);

                String description = buildUpdateDescription(oldUsername, oldEmail, oldName,
                        newUsername, newEmail, newName);

                activityLogHelper.logActivity(
                        ActionType.UPDATE,
                        EntityType.USER,
                        userId,
                        description,
                        buildOldValueString(oldUsername, oldEmail, oldName),
                        buildNewValueString(newUsername, newEmail, newName)
                );

                log.debug("User 수정 로그 기록 완료 - ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("User 수정 로그 기록 중 오류 발생", e);
        } finally {
            safeCleanupContext();
        }
    }

    // ==================== 회원탈퇴 ====================

    @Before("userDeletionMethods()")
    public void beforeUserDeletion(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                String username = (String) args[0];

                if (username != null) {
                    Optional<User> userOpt = userRepository.findByUsername(username);
                    if (userOpt.isPresent()) {
                        User oldUser = userOpt.get();

                        Map<String, Object> context = userContext.get();
                        context.put("userId", oldUser.getId());
                        context.put("deletedUsername", oldUser.getUsername());
                        context.put("deletedEmail", oldUser.getEmail());

                        log.debug("User 삭제 전 정보 저장 완료 - ID: {}, Username: {}", oldUser.getId(), username);
                    } else {
                        log.warn("사용자를 찾을 수 없습니다: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("User 삭제 전 정보 저장 실패: {}", e.getMessage());
        }
    }

    @AfterReturning(value = "userDeletionMethods()")
    public void logUserDeletion(JoinPoint joinPoint) {
        try {
            Map<String, Object> context = userContext.get();
            Long userId = (Long) context.get("userId");
            String deletedUsername = (String) context.get("deletedUsername");
            String deletedEmail = (String) context.get("deletedEmail");

            if (userId != null && deletedUsername != null) {
                String description = String.format("사용자 '%s'가 회원탈퇴했습니다.", deletedUsername);

                activityLogHelper.logActivity(
                        ActionType.DELETE,
                        EntityType.USER,
                        userId,
                        description,
                        String.format("%s (%s)", deletedUsername, deletedEmail),
                        null
                );

                log.debug("User 삭제 로그 기록 완료 - ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("User 삭제 로그 기록 중 오류 발생", e);
        } finally {
            safeCleanupContext();
        }
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 결과 객체에서 사용자명 추출
     */
    private String extractUsernameFromResult(Object result) {
        try {
            if (result == null) return null;

            var dataField = activityLogHelper.findFieldRecursively(result.getClass(), "data");
            if (dataField != null) {
                dataField.setAccessible(true);
                Object dataValue = dataField.get(result);
                if (dataValue != null) {
                    result = dataValue;
                }
            }

            var usernameField = activityLogHelper.findFieldRecursively(result.getClass(), "username");
            if (usernameField != null) {
                usernameField.setAccessible(true);
                Object value = usernameField.get(result);
                return value != null ? value.toString() : null;
            }
        } catch (Exception e) {
            log.debug("결과에서 사용자명 추출 실패: {}", e.getMessage());
        }
        return "알 수 없는 사용자";
    }

    /**
     * 결과 객체에서 이메일 추출
     */
    private String extractEmailFromResult(Object result) {
        try {
            if (result == null) return null;

            var emailField = activityLogHelper.findFieldRecursively(result.getClass(), "email");
            if (emailField != null) {
                emailField.setAccessible(true);
                Object value = emailField.get(result);
                return value != null ? value.toString() : null;
            }
        } catch (Exception e) {
            log.debug("결과에서 이메일 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 결과 객체에서 이름 추출
     */
    private String extractNameFromResult(Object result) {
        try {
            if (result == null) return null;

            var nameField = activityLogHelper.findFieldRecursively(result.getClass(), "name");
            if (nameField != null) {
                nameField.setAccessible(true);
                Object value = nameField.get(result);
                return value != null ? value.toString() : null;
            }
        } catch (Exception e) {
            log.debug("결과에서 이름 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 로그인 인자에서 사용자명 추출
     */
    private String extractUsernameFromLoginArgs(Object[] args) {
        try {
            if (args != null && args.length > 0) {
                Object loginDto = args[0];
                if (loginDto != null) {
                    var usernameField = activityLogHelper.findFieldRecursively(loginDto.getClass(), "username");
                    if (usernameField != null) {
                        usernameField.setAccessible(true);
                        Object value = usernameField.get(loginDto);
                        return value != null ? value.toString() : null;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("로그인 인자에서 사용자명 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 인자에서 User ID 추출
     */
    private Long extractUserIdFromArgs(Object[] args) {
        if (args == null || args.length == 0) return null;

        try {
            if (args[0] instanceof Long longValue) {
                return longValue;
            }
            if (args[0] instanceof Number numberValue) {
                return numberValue.longValue();
            }
            // DTO에서 ID 추출 시도
            if (args[0] != null) {
                Long id = activityLogHelper.extractIdFromResult(args[0]);
                if (id != null) return id;
            }
        } catch (Exception e) {
            log.debug("인자에서 User ID 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 사용자 수정 내용 설명 생성
     */
    private String buildUpdateDescription(String oldUsername, String oldEmail, String oldName,
                                          String newUsername, String newEmail, String newName) {
        StringBuilder changes = new StringBuilder();

        if (oldUsername != null && newUsername != null && !oldUsername.equals(newUsername)) {
            changes.append(String.format("사용자명: '%s' → '%s'", oldUsername, newUsername));
        }

        if (oldEmail != null && newEmail != null && !oldEmail.equals(newEmail)) {
            if (changes.length() > 0) changes.append(", ");
            changes.append(String.format("이메일: '%s' → '%s'", oldEmail, newEmail));
        }

        if (oldName != null && newName != null && !oldName.equals(newName)) {
            if (changes.length() > 0) changes.append(", ");
            changes.append(String.format("이름: '%s' → '%s'", oldName, newName));
        }

        return changes.length() > 0 ?
                "사용자 정보를 수정했습니다. " + changes.toString() :
                "사용자 정보를 수정했습니다.";
    }

    /**
     * 이전 값 문자열 생성
     */
    private String buildOldValueString(String username, String email, String name) {
        return String.format("사용자명: %s, 이메일: %s, 이름: %s",
                username != null ? username : "N/A",
                email != null ? email : "N/A",
                name != null ? name : "N/A");
    }

    /**
     * 새 값 문자열 생성
     */
    private String buildNewValueString(String username, String email, String name) {
        return String.format("사용자명: %s, 이메일: %s, 이름: %s",
                username != null ? username : "N/A",
                email != null ? email : "N/A",
                name != null ? name : "N/A");
    }

    /**
     * 안전한 ThreadLocal 정리 (메모리 누수 방지)
     */
    private void safeCleanupContext() {
        try {
            Map<String, Object> context = userContext.get();
            if (context != null) {
                context.clear();
            }
        } catch (Exception e) {
            log.debug("Context 정리 중 오류 발생: {}", e.getMessage());
        } finally {
            try {
                userContext.remove();
            } catch (Exception e) {
                log.debug("ThreadLocal 제거 중 오류 발생: {}", e.getMessage());
            }
        }
    }

    /**
     * 스프링 빈 소멸 시 ThreadLocal 정리
     */
    @PreDestroy
    public void cleanup() {
        try {
            userContext.remove();
            log.debug("UserActivityLogAspect ThreadLocal 정리 완료");
        } catch (Exception e) {
            log.warn("UserActivityLogAspect 정리 중 오류 발생: {}", e.getMessage());
        }
    }
}