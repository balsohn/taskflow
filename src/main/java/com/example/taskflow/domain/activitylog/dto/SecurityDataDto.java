package com.example.taskflow.domain.activitylog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 보안 데이터 DTO
 * 개인정보보호를 위해 마스킹된 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityDataDto {
    private String ipMasked;        // "192.168.1.***"
    private String browser;         // "Chrome 120.0"
}
