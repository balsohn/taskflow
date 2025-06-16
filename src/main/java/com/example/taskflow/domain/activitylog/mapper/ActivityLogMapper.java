package com.example.taskflow.domain.activitylog.mapper;

import com.example.taskflow.domain.activitylog.dto.ActivityLogResponse;
import com.example.taskflow.domain.activitylog.dto.SecurityDataDto;
import com.example.taskflow.domain.activitylog.dto.UserInfoDto;
import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.user.entity.User;

public class ActivityLogMapper {

    public static ActivityLogResponse toResponse(ActivityLog activityLog) {
        return null;
    }

    public static SecurityDataDto createSecurityData(String ipAddress, String userAgent) {
        return null;
    }

    private static UserInfoDto createUserInfo(User user) {
        return null;
    }
}
