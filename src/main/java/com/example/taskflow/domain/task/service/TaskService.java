package com.example.taskflow.domain.task.service;

import com.example.taskflow.domain.task.dto.request.TaskRequestDto;
import com.example.taskflow.domain.task.dto.response.TaskResponseDto;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
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
}
