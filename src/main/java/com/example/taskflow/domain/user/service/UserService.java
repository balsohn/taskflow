package com.example.taskflow.domain.user.service;

import com.example.taskflow.domain.user.dto.UserRequestDto;
import com.example.taskflow.domain.user.dto.UserResponseDto;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;


    // 유저 생성
    public ApiResponse signup(UserRequestDto userRequestDto) {

        // TODO : 비밀번호 암호화 필요
        log.info(userRequestDto.getUsername());
        log.info(userRequestDto.getEmail());
        log.info(userRequestDto.getName());
        log.info(userRequestDto.getPassword());

        User user = new User(userRequestDto.getUsername(), userRequestDto.getName(), userRequestDto.getPassword(), userRequestDto.getEmail());



        User saveUser = userRepository.save(user);

        return new ApiResponse<>(true, "sert",userRequestDto);
    }
}
