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

    private String type;

    private Long userId;

    private UserInfo user;

    private Long taskId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    private String description;

}
