package com.example.taskflow.domain.task.dto.response;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.Priority;
import com.example.taskflow.domain.task.enums.Status;
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
    private final Priority priority;
    private final LocalDate dueDate;
    private final Status status;
    private final LocalDateTime startedAt;
    private final LocalDateTime createdAt;
    private final UserInfo creator;
    private final UserInfo assignee;

    public static TaskResponseDto fromEntity(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .startedAt(task.getStartedAt())
                .createdAt(task.getCreatedAt())
                .creator(UserInfo.fromUser(task.getCreator()))
                .assignee(UserInfo.fromUser(task.getAssignee()))
                .build();
    }
}
