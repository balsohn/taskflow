package com.example.taskflow.domain.task;

import com.example.taskflow.domain.task.dto.request.TaskRequestDto;
import com.example.taskflow.domain.task.dto.response.TaskResponseDto;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.Priority;
import com.example.taskflow.domain.task.repository.TaskRepository;
import com.example.taskflow.domain.task.service.TaskService;
import com.example.taskflow.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void 태스크_저장_테스트() {
        // given
        TaskRequestDto requestDto = new TaskRequestDto("프론트엔드 오류 수정",
                "페이지 이동 시 로딩 문제 해결",
                Priority.HIGH,
                 3L,
                LocalDate.parse("2025-06-30"));

        User creator = new User();
        creator.setId(1L);
        creator.setName("a");
        creator.setEmail("sss");

        User assignee = new User();
        assignee.setId(2L);
        assignee.setName("b");
        assignee.setEmail("ddd");

        Task task = TaskRequestDto.toEntity(requestDto, creator, assignee);
        Task savedTask = Task.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .creator(creator)
                .assignee(assignee)
                .build();
        savedTask.setId(1L);

        // when
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // then
        TaskResponseDto result = taskService.saveTask(requestDto);

        assertNotNull(result);
        assertEquals("프론트엔드 오류 수정", result.getTitle());
        assertEquals("페이지 이동 시 로딩 문제 해결", result.getDescription());
        assertEquals(1L, result.getId());
    }
}
