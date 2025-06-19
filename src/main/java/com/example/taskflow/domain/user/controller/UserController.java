package com.example.taskflow.domain.user.controller;

import com.example.taskflow.domain.user.dto.DeleteUserRequestDto;
import com.example.taskflow.domain.user.dto.LoginRequestDto;
import com.example.taskflow.domain.user.dto.UserRequestDto;
import com.example.taskflow.domain.user.service.UserService;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 회원가입
    @PostMapping("/api/auth/register")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {

        ApiResponse signupDto = userService.createUser(userRequestDto);

        return new ResponseEntity<>(signupDto, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDto loginRequestDto) {

        ApiResponse login = userService.login(loginRequestDto);


        return new ResponseEntity<>(login, HttpStatus.OK);
    }

    // 유저 조회
    @GetMapping("/api/users/me")
    public ResponseEntity<ApiResponse> myProfile(@AuthenticationPrincipal User user) {

        ApiResponse myProfile = userService.myProfile(user.getUsername());

        return new ResponseEntity<>(myProfile, HttpStatus.OK);
    }

    // 유저 삭제
    @PostMapping("/api/auth/withdraw")
    public ResponseEntity<ApiResponse> deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody
                                                  DeleteUserRequestDto deleteUserRequestDto) {

        ApiResponse apiResponse = userService.deleteUser(userDetails.getUsername(),deleteUserRequestDto.getPassword());

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

//     로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<ApiResponse> logout(@AuthenticationPrincipal UserDetails userDetails){
//
//        userService.logout(userDetails.getUsername());
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
