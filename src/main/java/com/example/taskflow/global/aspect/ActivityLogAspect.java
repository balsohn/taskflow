package com.example.taskflow.global.aspect;

import com.example.taskflow.domain.activitylog.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * AOP를 활용한 활동 로그 자동 기록 시스템
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLogAspect {

    private final ActivityLogService activityLogService;

    /**
     * Task 관련 메서드들에 대한 Pointcut 정의
     */
    @Pointcut("execution(* com.example.taskflow.domain.task.service.*.*(..))")
    public void taskServiceMethods() {}

    /**
     * User 관련 메서드들에 대한 Pointcut 정의
     */
    @Pointcut("execution(* com.example.taskflow.domain.user.service.*.*(..))")
    public void userServiceMethods() {}

    /**
     * Comment 관련 메서드들에 대한 Pointcut 정의
     */
    @Pointcut("execution(* com.example.taskflow.domain.comment.service.*.*(..))")
    public void commentServiceMethods() {}
}
