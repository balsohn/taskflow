package com.example.taskflow.domain.dashboard.service;

import com.example.taskflow.domain.dashboard.dto.DashboardResponse;
import com.example.taskflow.domain.dashboard.repository.QTaskRepository;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final QTaskRepository qTaskRepository;
    private final UserRepository userRepository;
    public DashboardResponse stats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));

       return qTaskRepository.stats(user.getId());
    }
}
