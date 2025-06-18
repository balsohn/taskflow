package com.example.taskflow.domain.task.dto.response;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.TaskPriority;
import com.example.taskflow.domain.task.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class TaskResponseDto {

    private final Long id;
    private final String title;
    private final String description;
    private final TaskPriority priority;
    private final TaskStatus status;
    private final LocalDate dueDate;
    private final LocalDateTime startedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final UserInfo creator;
    private final UserInfo assignee;

    public static TaskResponseDto fromEntity(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .startedAt(task.getStartedAt())
                .createdAt(task.getCreatedAt())
                .modifiedAt(task.getModifiedAt())
                .creator(UserInfo.fromUser(task.getCreator()))
                .assignee(UserInfo.fromUser(task.getAssignee()))
                .build();
    }
}
