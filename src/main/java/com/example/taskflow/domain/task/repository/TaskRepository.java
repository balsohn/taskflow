package com.example.taskflow.domain.task.repository;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"assignee"})
    @Query("""
                SELECT t FROM Task t
                WHERE (:status IS NULL OR t.status = :status)
                AND (:keyword IS NULL OR t.title LIKE %:keyword% OR t.description LIKE %:keyword%)
                AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
                AND t.isDeleted = false
            """)
    Page<Task> getTasks(@Param("status") TaskStatus status,
                        @Param("keyword") String keyword,
                        @Param("assigneeId") Long assigneeId,
                        Pageable pageable);

    @Query("""
            SELECT t FROM Task t 
            LEFT JOIN FETCH t.assignee 
            WHERE t.id = :taskId
            AND t.isDeleted = false
            """)
    Optional<Task> findByIdWithAssigneeAndIsDeletedFalse(Long taskId);

    Optional<Task> findByIdAndIsDeletedFalse(Long taskId);
}
