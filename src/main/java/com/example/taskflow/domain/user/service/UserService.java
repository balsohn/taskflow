package com.example.taskflow.domain.user.service;

import com.example.taskflow.domain.user.dto.LoginRequestDto;
import com.example.taskflow.domain.user.dto.UserRequestDto;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 유저 생성
    public ApiResponse createUser(UserRequestDto userRequestDto) {

        String encodePassword = passwordEncoder.encode(userRequestDto.getPassword());

        User user = new User(userRequestDto.getUsername(), encodePassword, userRequestDto.getEmail(),userRequestDto.getName(),userRequestDto.getRole());

        User saveUser = userRepository.save(user);

        return ApiResponse.success("회원가입이 완료되었습니다.", saveUser);
    }


    public ApiResponse login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsernameOrElseThrow(loginRequestDto.getUsername());

        log.info(String.valueOf(user));

        if(!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // API 명세서와 똑같이 변경
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());
        userMap.put("role", user.getRole());
        userMap.put("name", user.getName());
        response.put("user", userMap);

        return ApiResponse.success("로그인 성공",response);
    }
}
