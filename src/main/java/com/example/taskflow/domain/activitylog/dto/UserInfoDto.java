package com.example.taskflow.domain.activitylog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 DTO
 * 활동  로그에서 사용자 정보를 표시할 때 사용
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private Long id;
    private String name;
    private String email;
}
