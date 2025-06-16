package com.example.taskflow.domain.task.entity;

import com.example.taskflow.domain.task.enums.Priority;
import com.example.taskflow.domain.task.enums.Status;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime startedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    @Builder
    public Task(String title, String description, Priority priority, LocalDate dueDate, Status status, User creator, User assignee) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.creator = creator;
        this.assignee = assignee;
    }
}
