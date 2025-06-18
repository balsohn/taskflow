package com.example.taskflow.domain.task.service;

import com.example.taskflow.domain.task.dto.request.TaskRequestDto;
import com.example.taskflow.domain.task.dto.response.StatusResponseDto;
import com.example.taskflow.domain.task.dto.response.TaskDetailResponseDto;
import com.example.taskflow.domain.task.dto.response.TaskResponseDto;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.TaskStatus;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.enums.UserRoleEnum;
import com.example.taskflow.global.common.dto.PageResponse;
import com.example.taskflow.global.exception.custom.InvalidStatusException;
import com.example.taskflow.global.exception.custom.TaskNotFoundException;
import com.example.taskflow.global.exception.custom.UnauthorizedActionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponseDto saveTask(TaskRequestDto requestDto) {
        User assignee = new User("kkk", "abc", "www", "김", UserRoleEnum.USER);
        assignee.setId(2L);

        User creator = new User("jsdom", "124344", "wwwd", "이", UserRoleEnum.USER);
        assignee.setId(1L);

        // Todo: 로그인 후 수정
        Task task = TaskRequestDto.toEntity(requestDto, creator, assignee);
        Task savedTask = taskRepository.save(task);

        return TaskResponseDto.fromEntity(savedTask);
    }

    /**
     * @param status  작업상태(TODO, IN_PROGRESS, DONE) 필터
     * @param keyword 검색 키워드: 제목과 설명에 들어가는 내용을 검색
     * @param page
     * @param size
     * @return 페이징된 태스크 목록
     */
    public PageResponse<TaskResponseDto> getTasks(TaskStatus status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

//        Page<TaskResponseDto> responseDtos = taskRepository.getTasks(status, keyword, pageable)
//                .map(TaskResponseDto::fromEntity);
        Page<Task> tasks = taskRepository.getTasks(status, keyword, pageable);

//        return PageResponse.of(responseDtos.getContent(), responseDtos);
        return PageResponse.of(tasks, TaskResponseDto::fromEntity);
    }

    /*
    Task 단건 조회 메서드
    creator, assignee, comments 연관객체의 정보를 모두 fetch
     */
    public TaskDetailResponseDto getTask(Long taskId) {
        Task foundTask = taskRepository.findByIdWithDetailAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new TaskNotFoundException("존재하지 않는 태스크입니다."));

        return TaskDetailResponseDto.fromEntity(foundTask);
    }

    /*
    Task 수정 메서드
    requestDto는 title, description, priority, dueDate, (assignId or status)
    조회할 때 연관객체 fetch 하지 않고 지연로딩
     */
    @Transactional
    public TaskResponseDto updateTask(Long taskId, TaskRequestDto requestDto) {
        // TODO: 유저 조회 메서드 사용
        User assignee = new User("kkk", "abc", "www", "김", UserRoleEnum.USER);
        assignee.setId(2L);

        Task foundTask = findByIdOrElseThrow(taskId);

        // dueDate는 수정으로 null 값이 들어갈경우 기한없음으로 처리
        foundTask.update(requestDto.getTitle(), requestDto.getDescription(), requestDto.getDueDate(), requestDto.getPriority(), assignee);

        return TaskResponseDto.fromEntity(foundTask);
    }

    @Transactional
    public StatusResponseDto updateStatus(Long taskId, TaskStatus newStatus) {
        Task foundTask = findByIdOrElseThrow(taskId);

        User loginUser = new User("kkk", "abc", "www", "김", UserRoleEnum.USER);
        loginUser.setId(2L);

        if (loginUser.getRole().equals(UserRoleEnum.USER) && !loginUser.getId().equals(foundTask.getId())) {
            throw new UnauthorizedActionException("해당 작업에 대한 권한이 없습니다.");
        }

        validateStatusChange(foundTask, newStatus);
        foundTask.updateStatus(newStatus);

        return StatusResponseDto.fromEntity(foundTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task foundTask = findByIdOrElseThrow(taskId);
        foundTask.delete();
    }

    private Task findByIdOrElseThrow(Long taskId) {
        return taskRepository.findByIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new TaskNotFoundException("해당 Task를 찾을 수 없습니다."));
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
