package com.example.taskflow.domain.task.service;

import com.example.taskflow.domain.task.dto.request.TaskRequestDto;
import com.example.taskflow.domain.task.dto.response.TaskResponseDto;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.TaskStatus;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.enums.UserRoleEnum;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.common.dto.PageResponse;
import com.example.taskflow.global.exception.custom.InvalidStatusException;
import com.example.taskflow.global.exception.custom.TaskNotFoundException;
import com.example.taskflow.global.exception.custom.UnauthorizedActionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskResponseDto saveTask(String username, TaskRequestDto requestDto) {
        User creator = userRepository.findByUsernameOrElseThrow(username);
        User assignee = userRepository.findByIdOrElseThrow(requestDto.getAssigneeId());

        // Todo: 로그인 후 수정
        Task task = TaskRequestDto.toEntity(requestDto, creator, assignee);
        Task savedTask = taskRepository.save(task);

        return TaskResponseDto.fromEntity(savedTask);
    }

    /**
     * @param status  작업상태(TODO, IN_PROGRESS, DONE) 필터
     * @param keyword 검색 키워드: 제목과 설명에 들어가는 내용을 검색
     * @param assigneeId 담당자 ID
     * @param page
     * @param size
     * @return 페이징된 태스크 목록
     */
    public PageResponse<TaskResponseDto> getTasks(TaskStatus status, String keyword, Long assigneeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));

        Page<Task> tasks = taskRepository.getTasks(status, keyword, assigneeId, pageable);

        return PageResponse.of(tasks, TaskResponseDto::fromEntity);
    }

    /*
    Task 단건 조회 메서드
    연관객체 assignee를 fetch join
     */
    public TaskResponseDto getTask(Long taskId) {
        Task foundTask = taskRepository.findByIdWithAssigneeAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new TaskNotFoundException("존재하지 않는 태스크입니다."));

        return TaskResponseDto.fromEntity(foundTask);
    }

    /*
    Task 수정 메서드
    requestDto는 title, description, priority, dueDate, assigneeId
    조회할 때 연관객체는 fetch join 하지 않고 지연로딩
     */
    @Transactional
    public TaskResponseDto updateTask(String username, Long taskId, TaskRequestDto requestDto) {
        User loginUser = userRepository.findByUsernameOrElseThrow(username);
        Task foundTask = findByIdOrElseThrow(taskId);
        User assignee = userRepository.findByIdOrElseThrow(requestDto.getAssigneeId());

        // Task 생성자와 담당자외에는 권한 없음
        validateAuthorized(loginUser, foundTask);

        // dueDate는 수정으로 null 값이 들어갈경우 기한없음으로 처리
        foundTask.update(requestDto.getTitle(), requestDto.getDescription(), requestDto.getDueDate(), requestDto.getPriority(), assignee);

        return TaskResponseDto.fromEntity(foundTask);
    }

    /*
    Task 상태 변경 메서드
    ADMIN과 담당자만이 변경 가능
    Task 상태는 다음 단계로만 변경 가능
     */
    @Transactional
    public TaskResponseDto updateStatus(String username, Long taskId, TaskStatus newStatus) {
        User loginUser = userRepository.findByUsernameOrElseThrow(username);
        Task foundTask = findByIdOrElseThrow(taskId);

        if (loginUser.getRole().equals(UserRoleEnum.USER) && !loginUser.getId().equals(foundTask.getAssignee().getId())) {
            throw new UnauthorizedActionException("해당 작업에 대한 권한이 없습니다.");
        }

        validateStatusChange(foundTask, newStatus);
        foundTask.updateStatus(newStatus);

        return TaskResponseDto.fromEntity(foundTask);
    }

    @Transactional
    public void deleteTask(String username, Long taskId) {
        User loginUser = userRepository.findByUsernameOrElseThrow(username);
        Task foundTask = findByIdOrElseThrow(taskId);

        validateAuthorized(loginUser, foundTask);
        foundTask.delete();
    }

    /*
    private 메서드
     */
    private Task findByIdOrElseThrow(Long taskId) {
        return taskRepository.findByIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new TaskNotFoundException("해당 Task를 찾을 수 없습니다."));
    }

    private void validateAuthorized(User loginUser, Task foundTask) {
        if (!(loginUser.getId().equals(foundTask.getCreator().getId()) ||
                loginUser.getId().equals(foundTask.getAssignee().getId()))) {
            throw new UnauthorizedActionException("해당 작업에 대한 권한이 없습니다.");
        }
    }

    private void validateStatusChange(Task task, TaskStatus newStatus) {
        switch (task.getStatus()) {
            case TODO:
                if (newStatus != TaskStatus.IN_PROGRESS) {
                    throw new InvalidStatusException("Task의 상태는 다음 순서로만 변경 가능합니다.");
                }
                // 시작시간 기록
                task.recordStartedAt();
                break;
            case IN_PROGRESS:
                if (newStatus != TaskStatus.DONE) {
                    throw new InvalidStatusException("Task의 상태는 다음 순서로만 변경 가능합니다.");
                }
                break;
            case DONE:
                throw new InvalidStatusException("이미 완료된 상태입니다.");
        }
    }
}
