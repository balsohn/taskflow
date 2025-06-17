package com.example.taskflow.domain.task.controller;

import com.example.taskflow.domain.task.dto.request.TaskRequestDto;
import com.example.taskflow.domain.task.dto.response.TaskDetailResponseDto;
import com.example.taskflow.domain.task.dto.response.TaskResponseDto;
import com.example.taskflow.domain.task.enums.Status;
import com.example.taskflow.domain.task.service.TaskService;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.dto.PageResponse;
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
    public ResponseEntity<ApiResponse<PageResponse<TaskResponseDto>>> getTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success("태스크 목록 조회에 성공하였습니다.", taskService.getTasks(status, keyword, page, size)));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDetailResponseDto>> getTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.success("태스크 상세 조회에 성공하였습니다.", taskService.getTask(taskId)));
    }
}
