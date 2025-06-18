package com.example.taskflow.domain.task.dto.request;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.TaskPriority;
import com.example.taskflow.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskRequestDto {

    @NotBlank(message = "제목은 비어 있을 수 없습니다.")
    @Size(min = 1, max = 100)
    private final String title;

    @Size(max = 1000)
    private final String description;

    @NotNull(message = "우선순위가 지정되지 않았습니다.")
    private final TaskPriority priority;

    @NotNull(message = "담당자가 지정되지 않았습니다.")
    private final Long assigneeId;

    private final LocalDateTime dueDate;

    public TaskRequestDto(String title, String description, TaskPriority priority, Long assigneeId, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
    }

    public static Task toEntity(TaskRequestDto dto, User creator, User assignee) {
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .dueDate(dto.getDueDate() == null ? LocalDateTime.now().plusWeeks(1) : dto.getDueDate())
                .creator(creator)
                .assignee(assignee)
                .build();
    }
}
