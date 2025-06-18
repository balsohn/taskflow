package com.example.taskflow.domain.task.dto.response;

import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class StatusResponseDto {

    private final Long id;

    private final TaskStatus status;

    private final LocalDateTime startedAt;

    private final LocalDateTime modifiedAt;

    public static StatusResponseDto fromEntity(Task task) {
        return StatusResponseDto.builder()
                .id(task.getId())
                .status(task.getStatus())
                .startedAt(task.getStartedAt())
                .modifiedAt(task.getModifiedAt())
                .build();
    }
}
