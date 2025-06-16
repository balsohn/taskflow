package com.example.taskflow.domain.task.controller;

import com.example.taskflow.domain.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TaskController {

    private final TaskService taskService;



}
