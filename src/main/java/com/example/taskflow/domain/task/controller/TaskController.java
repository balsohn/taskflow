package com.example.taskflow.domain.task.controller;

import com.example.taskflow.domain.task.dto.request.StatusRequestDto;
import com.example.taskflow.domain.task.dto.request.TaskRequestDto;
import com.example.taskflow.domain.task.dto.response.TaskResponseDto;
import com.example.taskflow.domain.task.enums.TaskStatus;
import com.example.taskflow.domain.task.service.TaskService;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@RestController
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponseDto>> saveTask(@AuthenticationPrincipal User user,
                                                                 @Validated @RequestBody TaskRequestDto requestDto) {
        String username = user.getUsername();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("태스크가 생성되었습니다.", taskService.saveTask(username, requestDto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TaskResponseDto>>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("태스크 목록 조회에 성공하였습니다.", taskService.getTasks(status, search, assigneeId, page, size)));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponseDto>> getTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.success("태스크 상세 조회에 성공하였습니다.", taskService.getTask(taskId)));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTask(@AuthenticationPrincipal User user,
                                                                   @PathVariable Long taskId,
                                                                   @Validated @RequestBody TaskRequestDto requestDto) {
        String username = user.getUsername();
        return ResponseEntity.ok(ApiResponse.success("태스크가 수정되었습니다.", taskService.updateTask(username, taskId, requestDto)));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateStatus(@AuthenticationPrincipal User user,
                                                                     @PathVariable Long taskId,
                                                                     @Validated @RequestBody StatusRequestDto requestDto) {
        String username = user.getUsername();
        return ResponseEntity.ok(
                ApiResponse.success("태스크 상태가 변경되었습니다.", taskService.updateStatus(username, taskId, requestDto.getStatus())));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@AuthenticationPrincipal User user,
                                                        @PathVariable Long taskId) {
        String username = user.getUsername();
        taskService.deleteTask(username, taskId);

        return ResponseEntity.ok(ApiResponse.success("태스크가 삭제되었습니다."));
    }
}
