package com.example.taskflow.domain.user.controller;

import com.example.taskflow.domain.user.dto.UserRequestDto;
import com.example.taskflow.domain.user.dto.UserResponseDto;
import com.example.taskflow.domain.user.service.UserService;
import com.example.taskflow.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse> signup(@RequestBody UserRequestDto userRequestDto){

        ApiResponse signupDto = userService.signup(userRequestDto);

        return new ResponseEntity<>(signupDto, HttpStatus.CREATED);
    }

}
