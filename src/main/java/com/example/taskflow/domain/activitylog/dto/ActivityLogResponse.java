package com.example.taskflow.domain.activitylog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 활동 로그 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {
    private Long id;
    private Long userId;
    private String actionType;
    private String entityType;
    private Long entityId;
    private String description;
    private String oldValue;
    private String newValue;
    private SecurityDataDto securityData;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;

    private UserInfoDto user;

}
