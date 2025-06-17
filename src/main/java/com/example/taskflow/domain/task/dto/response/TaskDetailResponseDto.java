package com.example.taskflow.domain.task.dto.response;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.Priority;
import com.example.taskflow.domain.task.enums.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TaskDetailResponseDto {

    private final Long id;
    private final String title;
    private final String description;
    private final Priority priority;
    private final Status status;
    private final LocalDate dueDate;
    private final LocalDateTime startedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final UserInfo creator;
    private final UserInfo assignee;

    private List<CommentInfo> comments;

    public static TaskDetailResponseDto fromEntity(Task task) {
        return TaskDetailResponseDto.builder()
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
                .comments(task.getComments().stream()
                        // TODO: comment 연동하고 동작하는지 확인
                        .map(comment -> CommentInfo.fromComment(comment))
                        .collect(Collectors.toList()))
                .build();
    }
}
