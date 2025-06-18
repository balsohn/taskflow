package com.example.taskflow.domain.task.controller;

import com.example.taskflow.domain.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;

}
