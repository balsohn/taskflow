package com.example.taskflow.global.aop;

import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.task.entity.Task;
import com.example.taskflow.domain.task.enums.TaskPriority;
import com.example.taskflow.domain.task.enums.TaskStatus;
import com.example.taskflow.domain.task.repository.TaskRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Task 도메인 활동 로그 Aspect
 * - 태스크 생성, 수정, 삭제, 상태 변경 로깅
 * - Before/After 지원
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskActivityLogAspect {

    private final ActivityLogHelper activityLogHelper;
    private final TaskRepository taskRepository;

    // 변경 전 값을 임시 저장하는 ThreadLocal Map
    private final ThreadLocal<Map<String, Object>> taskContext =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    // ==================== Pointcut 정의 ====================

    @Pointcut("execution(* com.example.taskflow.domain.task.service.*.*(..))")
    public void taskServiceMethods() {}

    @Pointcut("taskServiceMethods() && execution(* *..saveTask(..))")
    public void taskCreationMethods() {}

    @Pointcut("taskServiceMethods() && execution(* *..updateTask(..))")
    public void taskUpdateMethods() {}

    @Pointcut("taskServiceMethods() && execution(* *..updateStatus(..))")
    public void taskStatusChangeMethods() {}

    @Pointcut("taskServiceMethods() && execution(* *..deleteTask(..))")
    public void taskDeletionMethods() {}

    // ==================== 태스크 생성 ====================

    @AfterReturning(value = "taskCreationMethods()", returning = "result")
    public void logTaskCreation(JoinPoint joinPoint, Object result) {
        try {
            Long taskId = activityLogHelper.extractIdFromResult(result);
            String taskTitle = activityLogHelper.extractTitleFromResult(result);

            if (taskId != null) {
                String description = String.format("새로운 작업 '%s'가 생성되었습니다.", taskTitle);

                activityLogHelper.logSimpleActivity(
                        ActionType.CREATE,
                        EntityType.TASK,
                        taskId,
                        description
                );

                log.debug("Task 생성 로그 기록 완료 - ID: {}, 제목: {}", taskId, taskTitle);
            }
        } catch (Exception e) {
            log.error("Task 생성 로그 기록 중 오류 발생", e);
        }
    }

    // ==================== 태스크 수정 ====================

    @Before("taskUpdateMethods()")
    public void beforeTaskUpdate(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Long taskId = extractTaskIdFromArgs(args);

            if (taskId != null) {
                // 기존 태스크 정보를 조회해서 저장
                Optional<Task> taskOpt = taskRepository.findByIdAndIsDeletedFalse(taskId);
                if (taskOpt.isPresent()) {
                    Task oldTask = taskOpt.get();

                    Map<String, Object> context = taskContext.get();
                    context.put("taskId", taskId);
                    context.put("oldTitle", oldTask.getTitle());
                    context.put("oldPriority", oldTask.getPriority());
                    context.put("oldAssigneeId", oldTask.getAssignee().getId());
                    context.put("oldAssigneeName", oldTask.getAssignee().getName());

                    log.debug("Task 수정 전 정보 저장 완료 - ID: {}", taskId);
                }
            }
        } catch (Exception e) {
            log.debug("Task 수정 전 정보 저장 실패 (계속 진행): {}", e.getMessage());
        }
    }

    @AfterReturning(value = "taskUpdateMethods()", returning = "result")
    public void logTaskUpdate(JoinPoint joinPoint, Object result) {
        try {
            Map<String, Object> context = taskContext.get();
            Long taskId = (Long) context.get("taskId");

            if (taskId == null) {
                taskId = activityLogHelper.extractIdFromResult(result);
            }

            if (taskId != null) {
                // 변경 내용 확인 및 로그 기록
                logTaskFieldChanges(taskId, context, result);

                log.debug("Task 수정 로그 기록 완료 - ID: {}", taskId);
            }
        } catch (Exception e) {
            log.error("Task 수정 로그 기록 중 오류 발생", e);
        } finally {
            safeCleanupContext();
        }
    }

    /**
     * Task 필드별 변경 내용 로그 기록
     */
    private void logTaskFieldChanges(Long taskId, Map<String, Object> context, Object result) {
        try {
            // 새로운 태스크 정보 조회
            Optional<Task> newTaskOpt = taskRepository.findByIdAndIsDeletedFalse(taskId);
            if (newTaskOpt.isEmpty()) return;

            Task newTask = newTaskOpt.get();

            // 1. 제목 변경 확인
            String oldTitle = (String) context.get("oldTitle");
            String newTitle = newTask.getTitle();
            if (oldTitle != null && !oldTitle.equals(newTitle)) {
                activityLogHelper.logSimpleActivity(
                        ActionType.UPDATE,
                        EntityType.TASK,
                        taskId,
                        "작업 제목을 변경하였습니다."
                );
            }

            // 2. 우선순위 변경 확인
            TaskPriority oldPriority = (TaskPriority) context.get("oldPriority");
            TaskPriority newPriority = newTask.getPriority();
            if (oldPriority != null && !oldPriority.equals(newPriority)) {
                String oldPriorityDisplay = activityLogHelper.getTaskPriorityDisplay(oldPriority.name());
                String newPriorityDisplay = activityLogHelper.getTaskPriorityDisplay(newPriority.name());

                activityLogHelper.logBeforeAfterActivity(
                        ActionType.UPDATE,
                        EntityType.TASK,
                        taskId,
                        "작업 우선순위를 변경하였습니다",
                        oldPriorityDisplay,
                        newPriorityDisplay
                );
            }

            // 3. 담당자 변경 확인
            Long oldAssigneeId = (Long) context.get("oldAssigneeId");
            Long newAssigneeId = newTask.getAssignee().getId();
            if (oldAssigneeId != null && !oldAssigneeId.equals(newAssigneeId)) {
                String oldAssigneeName = (String) context.get("oldAssigneeName");
                String newAssigneeName = newTask.getAssignee().getName();

                activityLogHelper.logBeforeAfterActivity(
                        ActionType.UPDATE,
                        EntityType.TASK,
                        taskId,
                        "작업 담당자를 변경하였습니다",
                        oldAssigneeName != null ? oldAssigneeName : "알 수 없음",
                        newAssigneeName != null ? newAssigneeName : "알 수 없음"
                );
            }

        } catch (Exception e) {
            log.error("Task 필드 변경 로그 기록 중 오류 발생", e);
        }
    }

    // ==================== 태스크 상태 변경 ====================

    @Before("taskStatusChangeMethods()")
    public void beforeTaskStatusChange(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Long taskId = extractTaskIdFromArgs(args);

            if (taskId != null) {
                // 기존 상태 조회
                Optional<Task> taskOpt = taskRepository.findByIdAndIsDeletedFalse(taskId);
                if (taskOpt.isPresent()) {
                    TaskStatus oldStatus = taskOpt.get().getStatus();
                    TaskStatus newStatus = extractNewStatusFromArgs(args);

                    Map<String, Object> context = taskContext.get();
                    context.put("taskId", taskId);
                    context.put("oldStatus", oldStatus);
                    context.put("newStatus", newStatus);

                    log.debug("Task 상태 변경 전 정보 저장 완료 - ID: {}, {} → {}",
                            taskId, oldStatus, newStatus);
                }
            }
        } catch (Exception e) {
            log.debug("Task 상태 변경 전 정보 저장 실패: {}", e.getMessage());
        }
    }

    @AfterReturning(value = "taskStatusChangeMethods()", returning = "result")
    public void logTaskStatusChange(JoinPoint joinPoint, Object result) {
        try {
            Map<String, Object> context = taskContext.get();
            Long taskId = (Long) context.get("taskId");
            TaskStatus oldStatus = (TaskStatus) context.get("oldStatus");
            TaskStatus newStatus = (TaskStatus) context.get("newStatus");

            if (taskId != null && oldStatus != null && newStatus != null) {
                String oldStatusDisplay = activityLogHelper.getTaskStatusDisplay(oldStatus.name());
                String newStatusDisplay = activityLogHelper.getTaskStatusDisplay(newStatus.name());

                activityLogHelper.logBeforeAfterActivity(
                        ActionType.STATUS_CHANGE,
                        EntityType.TASK,
                        taskId,
                        "작업 상태를 변경하였습니다",
                        oldStatusDisplay,
                        newStatusDisplay
                );

                log.debug("Task 상태 변경 로그 기록 완료 - ID: {}, {} → {}",
                        taskId, oldStatusDisplay, newStatusDisplay);
            }
        } catch (Exception e) {
            log.error("Task 상태 변경 로그 기록 중 오류 발생", e);
        } finally {
            safeCleanupContext();
        }
    }

    // ==================== 태스크 삭제 ====================

    @Before("taskDeletionMethods()")
    public void beforeTaskDeletion(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Long taskId = extractTaskIdFromArgs(args);

            if (taskId != null) {
                // 삭제될 태스크 정보 저장
                Optional<Task> taskOpt = taskRepository.findByIdAndIsDeletedFalse(taskId);
                if (taskOpt.isPresent()) {
                    Task task = taskOpt.get();

                    Map<String, Object> context = taskContext.get();
                    context.put("taskId", taskId);
                    context.put("taskTitle", task.getTitle());

                    log.debug("Task 삭제 전 정보 저장 완료 - ID: {}, 제목: {}", taskId, task.getTitle());
                }
            }
        } catch (Exception e) {
            log.error("Task 삭제 전 정보 저장 실패: {}", e.getMessage());
        }
    }

    @AfterReturning(value = "taskDeletionMethods()")
    public void logTaskDeletion(JoinPoint joinPoint) {
        try {
            Map<String, Object> context = taskContext.get();
            Long taskId = (Long) context.get("taskId");
            String taskTitle = (String) context.get("taskTitle");

            if (taskId != null) {
                String description = taskTitle != null ?
                        String.format("작업 '%s'가 삭제되었습니다.", taskTitle) :
                        "작업이 삭제되었습니다.";

                activityLogHelper.logSimpleActivity(
                        ActionType.DELETE,
                        EntityType.TASK,
                        taskId,
                        description
                );

                log.debug("Task 삭제 로그 기록 완료 - ID: {}", taskId);
            }
        } catch (Exception e) {
            log.error("Task 삭제 로그 기록 중 오류 발생", e);
        } finally {
            safeCleanupContext();
        }
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 메서드 인자에서 Task ID 추출
     */
    private Long extractTaskIdFromArgs(Object[] args) {
        try {
            // TaskService 메서드들을 분석해보면:
            // saveTask(String username, TaskRequestDto requestDto) - ID 없음
            // updateTask(String username, Long taskId, TaskRequestDto requestDto) - args[1]이 taskId
            // updateStatus(String username, Long taskId, TaskStatus newStatus) - args[1]이 taskId
            // deleteTask(String username, Long taskId) - args[1]이 taskId

            if (args.length >= 2 && args[1] instanceof Long) {
                return (Long) args[1];
            }
        } catch (Exception e) {
            log.debug("인자에서 Task ID 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 메서드 인자에서 새로운 상태 추출
     */
    private TaskStatus extractNewStatusFromArgs(Object[] args) {
        try {
            // updateStatus(String username, Long taskId, TaskStatus newStatus) - args[2]가 newStatus
            if (args.length >= 3 && args[2] instanceof TaskStatus) {
                return (TaskStatus) args[2];
            }
        } catch (Exception e) {
            log.debug("인자에서 새 상태 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 안전한 ThreadLocal 정리 (메모리 누수 방지)
     */
    private void safeCleanupContext() {
        try {
            Map<String, Object> context = taskContext.get();
            if (context != null) {
                context.clear();
            }
        } catch (Exception e) {
            log.debug("Context 정리 중 오류 발생: {}", e.getMessage());
        } finally {
            try {
                taskContext.remove();
            } catch (Exception e) {
                log.debug("ThreadLocal 제거 중 오류 발생: {}", e.getMessage());
            }
        }
    }

    /**
     * 스프링 빈 소멸 시 ThreadLocal 정리
     */
    @PreDestroy
    public void cleanup() {
        try {
            taskContext.remove();
            log.debug("TaskActivityAspect ThreadLocal 정리 완료");
        } catch (Exception e) {
            log.warn("TaskActivityAspect 정리 중 오류 발생: {}", e.getMessage());
        }
    }
}