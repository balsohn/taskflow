package com.example.taskflow.domain.task.controller;

import com.example.taskflow.domain.task.dto.request.TaskRequestDto;
import com.example.taskflow.domain.task.dto.response.TaskResponseDto;
import com.example.taskflow.domain.task.service.TaskService;
import com.example.taskflow.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@RestController
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponseDto>> saveTask(@Validated @RequestBody TaskRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("태스크가 생성되었습니다.", taskService.saveTask(requestDto)));
    }

    @GetMapping
    public String hello() {
        return "ok";
    }

}
