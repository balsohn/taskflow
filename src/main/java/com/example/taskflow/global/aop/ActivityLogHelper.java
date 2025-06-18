package com.example.taskflow.global.aop;

import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.activitylog.service.ActivityLogService;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 활동 로그 관련 공통 기능을 제공하는 헬퍼 클래스 (단순 버전)
 * - 동기 처리로 단순화
 * - 안전한 사용자 정보 캐싱
 * - 향상된 예외 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLogHelper {

    private final ActivityLogService activityLogService;
    private final UserRepository userRepository;

    // 사용자 정보 캐싱을 위한 ThreadLocal
    private static final ThreadLocal<User> cachedUser = new ThreadLocal<>();

    // ==================== 공통 로깅 메서드 ====================

    /**
     * 현재 인증된 사용자 기준으로 활동 로그 기록
     */
    public void logActivity(ActionType actionType, EntityType entityType, Long entityId,
                            String description, String oldValue, String newValue) {
        try {
            User currentUser = getCurrentUser();

            if (currentUser == null) {
                log.warn("인증된 사용자를 찾을 수 없어 활동 로그를 기록하지 않습니다. 설명: {}", description);
                return;
            }

            logActivityWithUser(currentUser, actionType, entityType, entityId, description, oldValue, newValue);

        } catch (Exception e) {
            log.error("활동 로그 기록 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 특정 사용자 ID로 활동 로그 기록 (회원가입, 로그인 등에서 사용)
     */
    public void logActivityWithUserId(Long userId, ActionType actionType, EntityType entityType,
                                      Long entityId, String description, String oldValue, String newValue) {
        try {
            RequestInfo requestInfo = extractRequestInfo();

            activityLogService.createActivityLog(
                    userId,
                    actionType,
                    entityType,
                    entityId,
                    description,
                    oldValue,
                    newValue,
                    requestInfo.ipAddress(),
                    requestInfo.userAgent()
            );

            log.debug("활동 로그 기록 완료 - 사용자ID: {}, 액션: {}, 엔티티: {}, 설명: {}",
                    userId, actionType, entityType, description);

        } catch (Exception e) {
            log.error("활동 로그 기록 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 특정 사용자 객체로 활동 로그 기록 (DB 조회 최소화)
     */
    public void logActivityWithUser(User user, ActionType actionType, EntityType entityType,
                                    Long entityId, String description, String oldValue, String newValue) {
        try {
            RequestInfo requestInfo = extractRequestInfo();

            activityLogService.createActivityLog(
                    user.getId(),
                    actionType,
                    entityType,
                    entityId,
                    description,
                    oldValue,
                    newValue,
                    requestInfo.ipAddress(),
                    requestInfo.userAgent()
            );

            log.debug("활동 로그 기록 완료 - 사용자: {}, 액션: {}, 엔티티: {}, 설명: {}",
                    user.getUsername(), actionType, entityType, description);

        } catch (Exception e) {
            log.error("활동 로그 기록 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    // ==================== 사용자 인증 관련 ====================

    /**
     * 현재 사용자 조회 (캐싱 적용)
     */
    public User getCurrentUser() {
        try {
            // ThreadLocal 캐시 확인
            User cached = cachedUser.get();
            if (cached != null) {
                return cached;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }

            String username = authentication.getName();
            if (username == null || username.trim().isEmpty()) {
                return null;
            }

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                cachedUser.set(user); // 캐시에 저장
                return user;
            }

            log.warn("사용자를 찾을 수 없습니다. 사용자명: {}", username);
            return null;

        } catch (Exception e) {
            log.error("현재 사용자 조회 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 현재 인증된 사용자 ID 가져오기
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * ThreadLocal 캐시 정리
     */
    public static void clearUserCache() {
        cachedUser.remove();
    }

    // ==================== HTTP 요청 정보 추출 ====================

    /**
     * 요청 정보를 한 번에 추출하는 레코드
     */
    public record RequestInfo(String ipAddress, String userAgent) {}

    /**
     * HTTP 요청 정보 일괄 추출 (성능 최적화)
     */
    public RequestInfo extractRequestInfo() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            String realIp = extractRealIpAddress(request);
            String maskedIp = maskIpAddress(realIp);
            String simplifiedUserAgent = simplifyUserAgent(request.getHeader("User-Agent"));

            return new RequestInfo(maskedIp, simplifiedUserAgent);

        } catch (Exception e) {
            log.warn("HTTP 요청 정보 추출 중 오류 발생", e);
            return new RequestInfo("Unknown", "Unknown");
        }
    }

    /**
     * 클라이언트 IP 주소 가져오기 (마스킹 처리)
     */
    public String getClientIpAddress() {
        return extractRequestInfo().ipAddress();
    }

    /**
     * 사용자 에이전트 정보 가져오기 (간소화 처리)
     */
    public String getUserAgent() {
        return extractRequestInfo().userAgent();
    }

    // ==================== 데이터 추출 유틸리티 ====================

    /**
     * 안전한 ID 추출 (NPE 방지 강화 + ApiResponse 지원)
     */
    public Long extractIdFromResult(Object result) {
        if (result == null) return null;

        try {
            // ApiResponse인 경우 data 필드에서 추출
            Field dataField = findFieldRecursively(result.getClass(), "data");
            if (dataField != null) {
                dataField.setAccessible(true);
                Object dataValue = dataField.get(result);
                if (dataValue != null) {
                    result = dataValue; // data 필드의 값을 사용
                }
            }

            Field idField = findFieldRecursively(result.getClass(), "id");
            if (idField != null) {
                idField.setAccessible(true);
                Object value = idField.get(result);

                if (value instanceof Long longValue) {
                    return longValue;
                } else if (value instanceof Number numberValue) {
                    return numberValue.longValue();
                }
            }
        } catch (Exception e) {
            log.debug("결과에서 ID 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 안전한 제목 추출 (다양한 필드명 지원 + ApiResponse 지원)
     */
    public String extractTitleFromResult(Object result) {
        if (result == null) return "제목 없음";

        try {
            // ApiResponse인 경우 data 필드에서 추출
            Field dataField = findFieldRecursively(result.getClass(), "data");
            if (dataField != null) {
                dataField.setAccessible(true);
                Object dataValue = dataField.get(result);
                if (dataValue != null) {
                    result = dataValue; // data 필드의 값을 사용
                }
            }

            // 우선순위: title -> name -> comment -> content -> username
            String[] titleFields = {"title", "name", "comment", "content", "username"};

            for (String fieldName : titleFields) {
                Field field = findFieldRecursively(result.getClass(), fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    Object value = field.get(result);
                    if (value != null) {
                        return truncateText(value.toString(), 50);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("결과에서 제목 추출 실패: {}", e.getMessage());
        }
        return "제목 없음";
    }

    /**
     * 클래스 계층구조를 따라 필드를 재귀적으로 찾기
     */
    public Field findFieldRecursively(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 안전한 텍스트 자르기 (null 체크 강화)
     */
    public String truncateText(String text, int maxLength) {
        if (text == null || text.trim().isEmpty()) return null;
        if (maxLength <= 0) return text;

        String trimmed = text.trim();
        if (trimmed.length() <= maxLength) return trimmed;

        return trimmed.substring(0, maxLength - 3) + "...";
    }

    // ==================== 개인정보보호 처리 메서드 ====================

    /**
     * 향상된 실제 IP 주소 추출
     */
    private String extractRealIpAddress(HttpServletRequest request) {
        // 다양한 프록시 헤더 확인
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "X-Original-Forwarded-For",
                "CF-Connecting-IP", // Cloudflare
                "True-Client-IP"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 첫 번째 IP만 사용 (프록시 체인에서)
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 향상된 IP 주소 마스킹
     */
    private String maskIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return "Unknown";
        }

        String trimmed = ipAddress.trim();

        // IPv4 처리
        if (trimmed.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
            String[] parts = trimmed.split("\\.");
            if (parts.length == 4) {
                return String.format("%s.%s.%s.***", parts[0], parts[1], parts[2]);
            }
        }

        // IPv6 처리
        if (trimmed.contains(":") && trimmed.length() > 4) {
            return trimmed.substring(0, 4) + "***";
        }

        // localhost 처리
        if ("127.0.0.1".equals(trimmed) || "::1".equals(trimmed) || "localhost".equals(trimmed)) {
            return "localhost";
        }

        // 기타 경우
        return "***";
    }

    /**
     * 향상된 User-Agent 간소화
     */
    private String simplifyUserAgent(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return "Unknown";
        }

        String ua = userAgent.toLowerCase();

        // 봇/크롤러 감지
        if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider") || ua.contains("scraper")) {
            return "Bot/Crawler";
        }

        // 모바일 감지
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone") || ua.contains("ipad")) {
            return "Mobile Browser";
        }

        // 브라우저 감지 (더 정확한 순서)
        if (ua.contains("edg/") || ua.contains("edge/")) {
            return "Edge " + extractSimpleBrowserVersion(userAgent, "edg/", "edge/");
        } else if (ua.contains("chrome/") && !ua.contains("chromium")) {
            return "Chrome " + extractSimpleBrowserVersion(userAgent, "chrome/");
        } else if (ua.contains("firefox/")) {
            return "Firefox " + extractSimpleBrowserVersion(userAgent, "firefox/");
        } else if (ua.contains("safari/") && !ua.contains("chrome")) {
            return "Safari " + extractSimpleBrowserVersion(userAgent, "version/");
        } else if (ua.contains("opera/") || ua.contains("opr/")) {
            return "Opera " + extractSimpleBrowserVersion(userAgent, "opera/", "opr/");
        }

        return "Other Browser";
    }

    /**
     * 간단한 브라우저 버전 추출
     */
    private String extractSimpleBrowserVersion(String userAgent, String... prefixes) {
        for (String prefix : prefixes) {
            int index = userAgent.toLowerCase().indexOf(prefix);
            if (index != -1) {
                int start = index + prefix.length();
                int end = findVersionEnd(userAgent, start);

                if (end > start) {
                    String version = userAgent.substring(start, end);
                    String[] parts = version.split("\\.");
                    if (parts.length >= 1) {
                        return parts[0]; // 메이저 버전만
                    }
                }
            }
        }
        return "";
    }

    /**
     * 버전 문자열 끝 찾기
     */
    private int findVersionEnd(String userAgent, int start) {
        for (int i = start; i < userAgent.length(); i++) {
            char c = userAgent.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                return i;
            }
        }
        return userAgent.length();
    }

    /**
     * 스프링 빈 소멸 시 ThreadLocal 정리
     */
    @PreDestroy
    public void cleanup() {
        try {
            clearUserCache();
            log.debug("ActivityLogHelper ThreadLocal 정리 완료");
        } catch (Exception e) {
            log.warn("ActivityLogHelper 정리 중 오류 발생: {}", e.getMessage());
        }
    }
}