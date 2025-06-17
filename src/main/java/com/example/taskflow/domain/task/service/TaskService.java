package com.example.taskflow.domain.task.service;

import com.example.taskflow.domain.task.dto.request.TaskRequestDto;
import com.example.taskflow.domain.task.dto.response.TaskResponseDto;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.Status;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.global.common.dto.PageResponse;
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
        User assignee = new User();
        assignee.setId(2L);
        assignee.setName("b");
        assignee.setEmail("ddd");

        User creator = new User();
        creator.setId(1L);
        creator.setName("a");
        creator.setEmail("sss");

        // Todo: 로그인 후 수정
        Task task = TaskRequestDto.toEntity(requestDto, creator, assignee);
        Task savedTask = taskRepository.save(task);

        return TaskResponseDto.fromEntity(savedTask);
    }

    public PageResponse<TaskResponseDto> getTasks(Status status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

//        Page<TaskResponseDto> responseDtos = taskRepository.getTasks(status, keyword, pageable)
//                .map(TaskResponseDto::fromEntity);
        Page<Task> tasks = taskRepository.getTasks(status, keyword, pageable);

//        return PageResponse.of(responseDtos.getContent(), responseDtos);
        return PageResponse.of(tasks, TaskResponseDto::fromEntity);
    }
}
