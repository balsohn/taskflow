package com.example.taskflow.domain.activitylog.service;

import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.activitylog.repository.ActivityLogRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.exception.custom.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setName("테스트 사용자");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole("USER");
    }

    @Test
    @DisplayName("활동 로그 생성 성공")
    void createActivityLog_Success() {
        // given
        Long userId = testUser.getId();
        ActionType actionType = ActionType.CREATE;
        EntityType entityType = EntityType.TASK;
        Long entityId = 1L;
        String description = "새로운 태스크를 생성했습니다.";
        String oldValue = null;
        String newValue = "태스크 제목";
        String ipAddress = "192.168.1.100";
        String userAgent = "Mozilla/5.0 (Chrome/120.0)";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // when
        activityLogService.createActivityLog(
                userId, actionType, entityType, entityId,
                description, oldValue, newValue, ipAddress, userAgent
        );

        // then
        verify(userRepository).findById(userId);

        // ArgumentCaptor를 사용하여 저장된 ActivityLog 검증
        ArgumentCaptor<ActivityLog> logCaptor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository).save(logCaptor.capture());

        ActivityLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getUser().getId()).isEqualTo(userId);
        assertThat(savedLog.getActionType()).isEqualTo(actionType);
        assertThat(savedLog.getEntityType()).isEqualTo(entityType);
        assertThat(savedLog.getEntityId()).isEqualTo(entityId);
        assertThat(savedLog.getDescription()).isEqualTo(description);
        assertThat(savedLog.getOldValue()).isEqualTo(oldValue);
        assertThat(savedLog.getNewValue()).isEqualTo(newValue);
        assertThat(savedLog.getIpAddress()).isEqualTo(ipAddress);
        assertThat(savedLog.getUserAgent()).isEqualTo(userAgent);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그 생성 시 예외 발생")
    void createActivityLog_UserNotFound_ThrowsException() {
        // given
        Long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> activityLogService.createActivityLog(
                nonExistentUserId,
                ActionType.CREATE,
                EntityType.TASK,
                1L,
                "테스트 로그",
                null,
                null,
                "192.168.1.100",
                "Chrome"
        )).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다. ID: 999");

        // 검증
        verify(userRepository).findById(nonExistentUserId);
        verify(activityLogRepository, never()).save(any(ActivityLog.class));
    }

    @Test
    @DisplayName("다양한 액션 타입으로 로그 생성")
    void createActivityLog_VariousActionTypes() {
        // given
        Long userId = testUser.getId();
        Long entityId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // when - 여러 액션 타입의 로그 생성
        activityLogService.createActivityLog(
                userId, ActionType.CREATE, EntityType.TASK, entityId,
                "태스크 생성", null, "태스크 제목", "192.168.1.100", "Chrome"
        );
        activityLogService.createActivityLog(
                userId, ActionType.UPDATE, EntityType.TASK, entityId,
                "태스크 수정", "이전 제목", "새 제목", "192.168.1.100", "Chrome"
        );
        activityLogService.createActivityLog(
                userId, ActionType.STATUS_CHANGE, EntityType.TASK, entityId,
                "상태 변경", "TODO", "IN_PROGRESS", "192.168.1.100", "Chrome"
        );
        activityLogService.createActivityLog(
                userId, ActionType.DELETE, EntityType.TASK, entityId,
                "태스크 삭제", "태스크 제목", null, "192.168.1.100", "Chrome"
        );

        // then
        verify(userRepository, times(4)).findById(userId);
        verify(activityLogRepository, times(4)).save(any(ActivityLog.class));

        // ArgumentCaptor로 저장된 로그들의 액션 타입 검증
        ArgumentCaptor<ActivityLog> logCaptor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository, times(4)).save(logCaptor.capture());

        assertThat(logCaptor.getAllValues())
                .extracting(ActivityLog::getActionType)
                .containsExactly(
                        ActionType.CREATE,
                        ActionType.UPDATE,
                        ActionType.STATUS_CHANGE,
                        ActionType.DELETE
                );
    }

    @Test
    @DisplayName("다양한 엔티티 타입으로 로그 생성")
    void createActivityLog_VariousEntityTypes() {
        // given
        Long userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // when - 여러 엔티티 타입의 로그 생성
        activityLogService.createActivityLog(
                userId, ActionType.CREATE, EntityType.TASK, 1L,
                "태스크 생성", null, null, "192.168.1.100", "Chrome"
        );
        activityLogService.createActivityLog(
                userId, ActionType.CREATE, EntityType.COMMENT, 2L,
                "댓글 생성", null, null, "192.168.1.100", "Chrome"
        );
        activityLogService.createActivityLog(
                userId, ActionType.LOGIN, EntityType.USER, userId,
                "사용자 로그인", null, null, "192.168.1.100", "Chrome"
        );

        // then
        verify(userRepository, times(3)).findById(userId);
        verify(activityLogRepository, times(3)).save(any(ActivityLog.class));

        // ArgumentCaptor로 저장된 로그들의 엔티티 타입 검증
        ArgumentCaptor<ActivityLog> logCaptor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository, times(3)).save(logCaptor.capture());

        assertThat(logCaptor.getAllValues())
                .extracting(ActivityLog::getEntityType)
                .containsExactly(
                        EntityType.TASK,
                        EntityType.COMMENT,
                        EntityType.USER
                );
    }

    @Test
    @DisplayName("로그 생성 시 올바른 사용자 정보 확인")
    void createActivityLog_CorrectUserInfo() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        activityLogService.createActivityLog(
                1L, ActionType.CREATE, EntityType.TASK, 1L,
                "사용자 확인 테스트", null, null, "192.168.1.100", "Chrome"
        );

        // then
        ArgumentCaptor<ActivityLog> logCaptor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository).save(logCaptor.capture());

        ActivityLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getUser().getUsername()).isEqualTo("testuser");
        assertThat(savedLog.getUser().getName()).isEqualTo("테스트 사용자");
        assertThat(savedLog.getUser().getEmail()).isEqualTo("test@example.com");
    }
}