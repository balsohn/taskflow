package com.example.taskflow.domain.activitylog.repository;

import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.activitylog.enums.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // 전체 활동 로그 조회 (삭제되지 않은 것만)
    Page<ActivityLog> findByIsDeletedFalse(Pageable pageable);

    // 사용자별 활동 로그 조회
    Page<ActivityLog> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    // 필터링과 검색을 포함한 활동 로그 조회
    @Query("""
            SELECT al FROM ActivityLog al
            WHERE al.isDeleted = false
            AND (:actionType IS NULL OR al.actionType = :actionType)
            AND (:entityId IS NULL OR al.entityId = :entityId)
            AND (:startDate IS NULL OR al.startDate = :startDate)
            AND (:endDate IS NULL OR al.endDate = :endDate)
            AND (:userId IS NULL OR al.user.id = :userId)
            """)
    Page<ActivityLog> findActivityLogsWithFilters(
            @Param("actionType") ActionType actionType,
            @Param("entityId") Long entityId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId,
            Pageable pageable
            );

    // 사용자별 필터링과 검색을 포함한 활동 로그 조회
    @Query("""
            SELECT al FROM ActivityLog al
            WHERE al.isDeleted = false
            AND al.user.id = :userId
            AND (:actionType IS NULL OR al.actionType = :actionType)
            AND (:entityId IS NULL OR al.entityId = :entityId)
            AND (:startDate IS NULL OR al.startDate = :startDate)
            AND (:endDate IS NULL OR al.endDate = :endDate)
            """)
    Page<ActivityLog> findUserActivityLogsWithFilters(
            @Param("userId") Long userId,
            @Param("actionType") ActionType actionType,
            @Param("entityId") Long entityId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
