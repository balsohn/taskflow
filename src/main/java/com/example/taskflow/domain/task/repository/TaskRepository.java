package com.example.taskflow.domain.task.repository;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT t FROM Task t 
        WHERE (:status IS NULL OR t.status = :status)
          AND (:keyword IS NULL OR t.title LIKE %:keyword% OR t.description LIKE %:keyword%)
    """)
    Page<Task> getTasks(@Param("status") Status status,
                        @Param("keyword") String keyword,
                        Pageable pageable);

    // TODO
    @Query("""
    SELECT
    """)
    Optional<Task> findByIdWithComments(@Param("taskId") Long taskId);
}
