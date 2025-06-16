package com.example.taskflow.domain.task.dto.request;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskRequestDto {

    @NotBlank
    private final String title;
    private final String description;
    @NotBlank
    private final Priority priority;
    @NotBlank
    private final Long assigneeId;
    private final LocalDate dueDate;

    public TaskRequestDto(String title, String description, Priority priority, Long assigneeId, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
    }

    public static Task toEntity(TaskRequestDto dto, User assignee) {
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .dueDate(dto.getDueDate())
                .assignee(assignee)
                .build();
    }
}
