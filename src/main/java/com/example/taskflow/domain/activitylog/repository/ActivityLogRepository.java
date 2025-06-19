package com.example.taskflow.domain.activitylog.repository;

import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * 필터링과 검색을 포함한 활동 로그 조회
     * LEFT JOIN FETCH로 N+1 문제 해결
     */
    @Query("""
            SELECT al FROM ActivityLog al
            LEFT JOIN FETCH al.user
            WHERE al.isDeleted = false
            AND (:actionType IS NULL OR al.actionType = :actionType)
            AND (:entityType IS NULL OR al.entityType = :entityType)
            AND (:entityId IS NULL OR al.entityId = :entityId)
            AND (:startDate IS NULL OR al.createdAt >= :startDate)
            AND (:endDate IS NULL OR al.createdAt <= :endDate)
            AND (:userId IS NULL OR al.user.id = :userId)
            ORDER BY al.createdAt DESC
            """)
    Page<ActivityLog> findActivityLogsWithFilters(
            @Param("actionType") ActionType actionType,
            @Param("entityType") EntityType entityType,
            @Param("entityId") Long entityId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
