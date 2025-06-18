package com.example.taskflow.global.aop;

import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.enums.UserRoleEnum;
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
 * - 회원가입, 로그인, 회원탈퇴 로깅
 * - 안전한 ThreadLocal 관리
 * - 향상된 예외 처리
 *
 * - 비활성화 (주석)
 *  회원정보 수정, - Before/After 지원 (권한 변경, 이메일 변경)
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

//    @Before("userUpdateMethods()")
//    public void beforeUserUpdate(JoinPoint joinPoint) {
//        try {
//            Object[] args = joinPoint.getArgs();
//            // TODO: UserService.update 메서드가 구현되면 정확한 인자 구조에 맞춰 수정 필요
//            String username = extractUsernameFromArgs(args);
//
//            if (username != null) {
//                Optional<User> userOpt = userRepository.findByUsername(username);
//                if (userOpt.isPresent()) {
//                    User oldUser = userOpt.get();
//
//                    Map<String, Object> context = userContext.get();
//                    context.put("userId", oldUser.getId());
//                    context.put("username", username);
//                    context.put("oldEmail", oldUser.getEmail());
//                    context.put("oldRole", oldUser.getRole());
//                    context.put("oldName", oldUser.getName());
//
//                    log.debug("User 수정 전 값 저장 완료 - ID: {}, Username: {}", oldUser.getId(), username);
//                }
//            }
//        } catch (Exception e) {
//            log.debug("User 수정 전 값 저장 실패 (계속 진행): {}", e.getMessage());
//        }
//    }
//
//    @AfterReturning(value = "userUpdateMethods()", returning = "result")
//    public void logUserUpdate(JoinPoint joinPoint, Object result) {
//        try {
//            Map<String, Object> context = userContext.get();
//            Long userId = (Long) context.get("userId");
//            String username = (String) context.get("username");
//
//            if (userId == null) {
//                // context에 없으면 result에서 추출 시도
//                userId = activityLogHelper.extractIdFromResult(result);
//            }
//
//            if (userId != null) {
//                // 변경된 사용자 정보 다시 조회
//                Optional<User> newUserOpt = userRepository.findById(userId);
//                if (newUserOpt.isPresent()) {
//                    User newUser = newUserOpt.get();
//
//                    // 각 필드별 변경 내용 확인 및 로그 기록
//                    logUserFieldChanges(userId, context, newUser);
//                }
//
//                log.debug("User 수정 로그 기록 완료 - ID: {}", userId);
//            }
//        } catch (Exception e) {
//            log.error("User 수정 로그 기록 중 오류 발생", e);
//        } finally {
//            safeCleanupContext();
//        }
//    }
//
//    /**
//     * User 필드별 변경 내용 로그 기록
//     */
//    private void logUserFieldChanges(Long userId, Map<String, Object> context, User newUser) {
//        try {
//            // 1. 이메일 변경 확인
//            String oldEmail = (String) context.get("oldEmail");
//            String newEmail = newUser.getEmail();
//            if (oldEmail != null && newEmail != null && !oldEmail.equals(newEmail)) {
//                activityLogHelper.logBeforeAfterActivity(
//                        ActionType.UPDATE,
//                        EntityType.USER,
//                        userId,
//                        "이메일을 변경하였습니다",
//                        oldEmail,
//                        newEmail
//                );
//            }
//
//            // 2. 권한 변경 확인 (관리자가 다른 사용자 권한 변경하는 경우)
//            UserRoleEnum oldRole = (UserRoleEnum) context.get("oldRole");
//            UserRoleEnum newRole = newUser.getRole();
//            if (oldRole != null && newRole != null && !oldRole.equals(newRole)) {
//                String oldRoleDisplay = activityLogHelper.getUserRoleDisplay(oldRole.name());
//                String newRoleDisplay = activityLogHelper.getUserRoleDisplay(newRole.name());
//
//                activityLogHelper.logBeforeAfterActivity(
//                        ActionType.UPDATE,
//                        EntityType.USER,
//                        userId,
//                        "사용자 권한을 변경하였습니다",
//                        oldRoleDisplay,
//                        newRoleDisplay
//                );
//            }
//
//            // 3. 이름 변경 확인 (단순 로그)
//            String oldName = (String) context.get("oldName");
//            String newName = newUser.getName();
//            if (oldName != null && newName != null && !oldName.equals(newName)) {
//                activityLogHelper.logSimpleActivity(
//                        ActionType.UPDATE,
//                        EntityType.USER,
//                        userId,
//                        "사용자 정보를 수정하였습니다."
//                );
//            }
//
//        } catch (Exception e) {
//            log.error("User 필드 변경 로그 기록 중 오류 발생", e);
//        }
//    }

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
                        context.put("deletedName", oldUser.getName());

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
            String deletedName = (String) context.get("deletedName");

            if (userId != null && deletedUsername != null) {
                String displayName = deletedName != null ? deletedName : deletedUsername;
                String description = String.format("사용자 '%s'가 회원탈퇴했습니다.", displayName);

                activityLogHelper.logSimpleActivity(
                        ActionType.DELETE,
                        EntityType.USER,
                        userId,
                        description
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

            var dataField = activityLogHelper.findFieldRecursively(result.getClass(), "data");
            if (dataField != null) {
                dataField.setAccessible(true);
                Object dataValue = dataField.get(result);
                if (dataValue != null) {
                    result = dataValue;
                }
            }

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

//    /**
//     * 인자에서 사용자명 추출 (수정 메서드용)
//     */
//    private String extractUsernameFromArgs(Object[] args) {
//        try {
//            // TODO: UserService.update 메서드 시그니처 확인 후 정확한 인덱스 조정 필요
//            // 현재는 첫 번째 인자가 username이라고 가정
//            if (args != null && args.length > 0 && args[0] instanceof String) {
//                return (String) args[0];
//            }
//        } catch (Exception e) {
//            log.debug("인자에서 사용자명 추출 실패: {}", e.getMessage());
//        }
//        return null;
//    }

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
            log.debug("UserActivityAspect ThreadLocal 정리 완료");
        } catch (Exception e) {
            log.warn("UserActivityAspect 정리 중 오류 발생: {}", e.getMessage());
        }
    }
}