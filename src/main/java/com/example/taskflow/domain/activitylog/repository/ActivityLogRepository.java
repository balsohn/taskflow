package com.example.taskflow.domain.activitylog.repository;

import com.example.taskflow.domain.activitylog.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // 전체 활동 로그 조회 (삭제되지 않은 것만)
    Page<ActivityLog> findByIsDeletedFalse(Pageable pageable);

    // 사용자별 활동 로그 조회
    Page<ActivityLog> findByUserUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
