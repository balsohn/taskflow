package com.example.taskflow.domain.activitylog.mapper;

import com.example.taskflow.domain.activitylog.dto.ActivityLogResponse;
import com.example.taskflow.domain.activitylog.dto.SecurityDataDto;
import com.example.taskflow.domain.activitylog.dto.UserInfoDto;
import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.user.entity.User;

public class ActivityLogMapper {

    public static ActivityLogResponse toResponse(ActivityLog activityLog) {
        return new ActivityLogResponse(
                activityLog.getId(),
                activityLog.getUser().getUserId(),
                activityLog.getActionType().name(),
                activityLog.getEntityType().name(),
                activityLog.getEntityId(),
                activityLog.getDescription(),
                activityLog.getOldValue(),
                activityLog.getNewValue(),
                createSecurityData(activityLog.getIpAddress(), activityLog.getUserAgent()),
                activityLog.getCreatedAt(),
                createUserInfo(activityLog.getUser())
        );
    }

    public static SecurityDataDto createSecurityData(String ipAddress, String userAgent) {
        String maskedIp = maskIpAddress(ipAddress);
        String simplifiedBrowser = simplifyUserAgent(userAgent);
        return new SecurityDataDto(maskedIp, simplifiedBrowser);
    }

    private static UserInfoDto createUserInfo(User user) {
        return new UserInfoDto(
                user.getUserId(),
                user.getName(),
                user.getEmail()
        );
    }

    /**
     * IP 주소 마스킹 (개인정보보호)
     * 192.168.1.100 -> 192.168.1.***
     */
    private static String maskIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return "Unknown";
        }

        // IPv4 주소 마스킹
        if (ipAddress.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            String[] parts = ipAddress.split("\\.");
            if (parts.length == 4) {
                return String.format("%s.%s.%s.***", parts[0], parts[1], parts[2]);
            }
        }

        // 기타 형식은 간단히 마스킹
        return "***";
    }

    private static String simplifyUserAgent(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return "Unknown";
        }

        String ua = userAgent.toLowerCase();

        // 브라우저 감지 (간단한 버전)
        if (ua.contains("chrome") && !ua.contains("edge")) {
            return "Chrome";
        } else if (ua.contains("firefox")) {
            return "Firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            return "Safari";
        } else if (ua.contains("edge")) {
            return "Edge";
        } else if (ua.contains("opera")) {
            return "Opera";
        }

        return "Other";
    }


}