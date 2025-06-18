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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String name;

    private String actionType;

    private String entityType;

    private Long entityId;

    private String description;

}
