package com.example.taskflow.domain.user.service;

import com.example.taskflow.domain.user.dto.LoginRequestDto;
import com.example.taskflow.domain.user.dto.UserRegisterResponseDto;
import com.example.taskflow.domain.user.dto.UserRequestDto;
import com.example.taskflow.domain.user.dto.UserResponseDto;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.enums.UserRoleEnum;
import com.example.taskflow.domain.user.repository.UserRepository;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.dto.TokenResponseDto;
import com.example.taskflow.global.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 유저 생성
    public ApiResponse createUser(UserRequestDto userRequestDto) {

        if(userRepository.existsByUsername(userRequestDto.getUsername())){
            return ApiResponse.error("중복된 Username이 있습니다");
        }

        if(userRepository.existsByemail(userRequestDto.getEmail())){
            return ApiResponse.error("중복된 Email이 있습니다.");
        }

        String encodePassword = passwordEncoder.encode(userRequestDto.getPassword());

        User user = new User(userRequestDto.getUsername(), encodePassword, userRequestDto.getEmail(),userRequestDto.getName(),userRequestDto.getRole());

        user.setRole(UserRoleEnum.USER);

        User saveUser = userRepository.save(user);

        return ApiResponse.success("회원가입이 완료되었습니다.",new UserRegisterResponseDto(saveUser.getId(),
                saveUser.getUsername(),
                saveUser.getEmail(),
                saveUser.getRole(),
                saveUser.getName(),
                saveUser.getCreatedAt(),
                saveUser.getModifiedAt()));
    }

    // 로그인 로직
    public ApiResponse login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsernameOrElseThrow(loginRequestDto.getUsername());

        if(user.getIsDeleted()){
            return ApiResponse.error("등록되지 않은 유저입니다.");
        }

        if(!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){
            return ApiResponse.error("잘못된 사용자명 또는 비밀번호입니다");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ApiResponse.success("로그인 성공", new TokenResponseDto(token));
    }

    // 프로필 조회
    public ApiResponse myProfile(String username) {

        User saveUser = userRepository.findByUsernameOrElseThrow(username);

        if(saveUser.getIsDeleted()){
            return ApiResponse.error("등록되지 않은 유저입니다.");
        }

        return ApiResponse.success("사용자 정보 조회 성공",new UserResponseDto(saveUser.getId(),
                saveUser.getUsername(),
                saveUser.getEmail(),
                saveUser.getName(),
                saveUser.getRole(),
                saveUser.getCreatedAt()));
    }

    // 회원 탈퇴 로직
    @Transactional
    public ApiResponse deleteUser(String username ,String password) {

        User user = userRepository.findByUsernameOrElseThrow(username);

        if(!passwordEncoder.matches(password,user.getPassword())){
            return ApiResponse.error("비밀번호가 일치하지 않습니다.");
        }

        user.delete();

        return ApiResponse.success("회원탈퇴가 완료되었습니다.");
    }

//    public ApiResponse logout(String username) {
//
//        User user = userRepository.findByUsernameOrElseThrow(username);
//
//
//
//        return ApiResponse.success("로그아웃 되었습니다.");
//    }
}
