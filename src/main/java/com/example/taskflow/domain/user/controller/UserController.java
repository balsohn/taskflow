package com.example.taskflow.domain.user.controller;

import com.example.taskflow.domain.user.dto.LoginRequestDto;
import com.example.taskflow.domain.user.dto.UserRequestDto;
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

    // 회원가입
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserRequestDto userRequestDto){

        ApiResponse signupDto = userService.createUser(userRequestDto);

        return new ResponseEntity<>(signupDto, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDto loginRequestDto) {

        ApiResponse login = userService.login(loginRequestDto);

        return new ResponseEntity<>(login,HttpStatus.OK);
    }
}
