package com.example.taskflow.domain.task.dto.response;

import com.example.taskflow.domain.task.enums.Priority;
import com.example.taskflow.domain.task.enums.Status;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TaskResponseDto {

    private final Long id;
    private final String title;
    private final String description;
    private final Priority priority;
    private final LocalDate dueDate;
    private final Status status;
    private final Boolean isDeleted;
    private final LocalDateTime createdAt;
    private final UserInfo creator;
    private final UserInfo assignee;

    public TaskResponseDto(Long id, String title, String description, Priority priority, LocalDate dueDate, Status status, Boolean isDeleted, LocalDateTime createdAt, UserInfo creator, UserInfo assignee) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.creator = creator;
        this.assignee = assignee;
    }
}
