package com.example.taskflow.global.common.filter;


import com.example.taskflow.domain.user.enums.UserRoleEnum;
import com.example.taskflow.global.common.utils.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Slf4j(topic = "JwtFilter")
@RequiredArgsConstructor
@Component
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        String username = null;
        String jwt = null;

        String authorizationHeader = httpRequest.getHeader("Authorization");

        // 처음 로그인 하는 거야? 그럼 JWT 토큰이 없을 것이니 토큰 먼저 발급 받아!
        if( requestURI.equals("/api/users") || requestURI.equals("/api/users/login")) {
            chain.doFilter(request,response);
            return;
        }

        // 로그인 하는게 아니네? 그럼 JWT 토큰 있어?
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("JWT 토큰이 필요 합니다.");
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요 합니다.");
            return;
        }

        // JWT 토큰이 있구만 그럼 JWT 토큰 유효해?
        // 1. Secret Key 내가 만든 거랑 동일해?
        // 2. JWT 시간 만료 된거 아니야?

        jwt = authorizationHeader.substring(7);

        // Secret Key 는 내가 만든게 맞는지 검증 만료 기간 지났는지 검증
        // Key , 만료기간이 지났는지 지나지 않았는지
        if (!jwtUtil.validateToken(jwt)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("{\"error\": \"Unauthorized\"}");
        }

        // JWT 사용자의 이름을 확인 해보자
        username = jwtUtil.extractUsername(jwt);

        String auth = jwtUtil.extractRoles(jwt);
        UserRoleEnum userRole = UserRoleEnum.valueOf(auth);
        User user = new User(username,"", List.of(userRole::getRole));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        chain.doFilter(request, response);
    }
}