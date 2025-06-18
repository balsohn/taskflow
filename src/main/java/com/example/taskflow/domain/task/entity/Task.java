package com.example.taskflow.domain.task.entity;

import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.task.enums.TaskPriority;
import com.example.taskflow.domain.task.enums.TaskStatus;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    private LocalDateTime startedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Task(String title, String description, TaskPriority priority, LocalDateTime dueDate, User creator, User assignee) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.creator = creator;
        this.assignee = assignee;
    }

    public void recordStartedAt() {
        this.startedAt = LocalDateTime.now();
    }

    public void updateStatus(TaskStatus status) {
        this.status = status;
    }

    // 테스트용
    public void setId(long l) {
        this.id = l;
    }

    public void update(String title, String description, LocalDateTime dueDate, TaskPriority priority, User assignee) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.assignee = assignee;
    }
}
