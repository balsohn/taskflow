package com.example.taskflow.domain.user.controller;

import com.example.taskflow.domain.user.dto.LoginRequestDto;
import com.example.taskflow.domain.user.dto.UserRequestDto;
import com.example.taskflow.domain.user.service.UserService;
import com.example.taskflow.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserRequestDto userRequestDto){

        ApiResponse signupDto = userService.createUser(userRequestDto);

        return new ResponseEntity<>(signupDto, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDto loginRequestDto) {

        ApiResponse login = userService.login(loginRequestDto);

        return new ResponseEntity<>(login,HttpStatus.OK);
    }

    // 유저 조회
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> myProfile(@AuthenticationPrincipal User user){

        String username = user.getUsername();

        ApiResponse myProfile = userService.myProfile(username);

        return new ResponseEntity<>(myProfile,HttpStatus.OK);
    }

    // 유저 삭제

}
