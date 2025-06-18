package com.example.taskflow.domain.task.dto.request;
import com.example.taskflow.domain.task.enums.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StatusRequestDto {

    @NotBlank(message = "태스크 상태를 지정해주세요.")
    private final TaskStatus status;

    public StatusRequestDto(TaskStatus status) {
        this.status = status;
    }
}
