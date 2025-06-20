package com.example.taskflow.global.aop;

import com.example.taskflow.domain.activitylog.enums.ActionType;
import com.example.taskflow.domain.activitylog.enums.EntityType;
import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.comment.repository.CommentRepository;
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
 * Comment 도메인 활동 로그 Aspect
 * - 댓글 생성, 수정, 삭제 로깅
 * - Before/After 지원
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CommentActivityLogAspect {

    private final ActivityLogHelper activityLogHelper;
    // TODO: CommentResponse는 실제로는 Repository인데 인터페이스명이 Response로 되어있음. 나중에 CommentRepository로 변경 필요
    private final CommentRepository commentRepository; // CommentRepository

    // 변경 전 값을 임시 저장하는 ThreadLocal Map
    private final ThreadLocal<Map<String, Object>> commentContext =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    // ==================== Pointcut 정의 ====================

    @Pointcut("execution(* com.example.taskflow.domain.comment.service.*.*(..))")
    public void commentServiceMethods() {}

    // TODO: 메서드명이 singup인데 signup으로 수정 필요 (오타)
    @Pointcut("commentServiceMethods() && execution(* *..signup(..))")
    public void commentCreationMethods() {}

    @Pointcut("commentServiceMethods() && execution(* *..update*(..))")
    public void commentUpdateMethods() {}

    @Pointcut("commentServiceMethods() && execution(* *..deleteComment(..))")
    public void commentDeletionMethods() {}

    // ==================== 댓글 생성 ====================

    @AfterReturning(value = "commentCreationMethods()", returning = "result")
    public void logCommentCreation(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            // TODO: CommentService.signup(Long id, String detail) 메서드 시그니처 확인 후 정확한 인자 위치 조정 필요
            Long taskId = extractTaskIdFromArgs(args);

            // result에서 댓글 ID 추출 시도
            Long commentId = activityLogHelper.extractIdFromResult(result);
            String commentContent = extractCommentContentFromResult(result);

            // 로그에 사용할 entityId 결정 (댓글 ID가 있으면 댓글 ID, 없으면 task ID)
            Long entityId = commentId != null ? commentId : taskId;

            if (entityId != null) {
                String description = commentContent != null ?
                        String.format("댓글을 작성했습니다: '%s'", activityLogHelper.truncateText(commentContent, 30)) :
                        "댓글을 작성했습니다.";

                activityLogHelper.logSimpleActivity(
                        ActionType.CREATE,
                        EntityType.COMMENT,
                        entityId,
                        description
                );

                log.debug("Comment 생성 로그 기록 완료 - Comment ID: {}, Task ID: {}", commentId, taskId);
            }
        } catch (Exception e) {
            log.error("Comment 생성 로그 기록 중 오류 발생", e);
        }
    }

    // ==================== 댓글 수정 ====================

    @Before("commentUpdateMethods()")
    public void beforeCommentUpdate(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            // TODO: Comment 수정 메서드가 실제로 구현되면 정확한 인자 구조에 맞춰 수정 필요
            Long commentId = extractCommentIdFromArgs(args);

            if (commentId != null) {
                // 기존 댓글 내용 조회
                Optional<Comment> commentOpt = commentRepository.findById(commentId);
                if (commentOpt.isPresent()) {
                    Comment oldComment = commentOpt.get();

                    Map<String, Object> context = commentContext.get();
                    context.put("commentId", commentId);
                    context.put("oldContent", oldComment.getContent());

                    log.debug("Comment 수정 전 정보 저장 완료 - ID: {}", commentId);
                }
            }
        } catch (Exception e) {
            log.debug("Comment 수정 전 정보 저장 실패 (계속 진행): {}", e.getMessage());
        }
    }

    @AfterReturning(value = "commentUpdateMethods()", returning = "result")
    public void logCommentUpdate(JoinPoint joinPoint, Object result) {
        try {
            Map<String, Object> context = commentContext.get();
            Long commentId = (Long) context.get("commentId");

            if (commentId == null) {
                commentId = activityLogHelper.extractIdFromResult(result);
            }

            if (commentId != null) {
                // TODO: Before/After 댓글 내용 비교해서 상세한 로그 기록도 고려해볼 수 있음
                activityLogHelper.logSimpleActivity(
                        ActionType.UPDATE,
                        EntityType.COMMENT,
                        commentId,
                        "댓글을 수정하였습니다."
                );

                log.debug("Comment 수정 로그 기록 완료 - ID: {}", commentId);
            }
        } catch (Exception e) {
            log.error("Comment 수정 로그 기록 중 오류 발생", e);
        } finally {
            safeCleanupContext();
        }
    }

    // ==================== 댓글 삭제 ====================

    @Before("commentDeletionMethods()")
    public void beforeCommentDeletion(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            // TODO: Comment 삭제 메서드가 실제로 구현되면 정확한 인자 구조에 맞춰 수정 필요
            Long commentId = extractCommentIdFromArgs(args);

            if (commentId != null) {
                // 삭제될 댓글 정보 저장
                Optional<Comment> commentOpt = commentRepository.findById(commentId);
                if (commentOpt.isPresent()) {
                    Comment comment = commentOpt.get();

                    Map<String, Object> context = commentContext.get();
                    context.put("commentId", commentId);
                    context.put("commentContent", comment.getContent());

                    log.debug("Comment 삭제 전 정보 저장 완료 - ID: {}", commentId);
                }
            }
        } catch (Exception e) {
            log.error("Comment 삭제 전 정보 저장 실패: {}", e.getMessage());
        }
    }

    @AfterReturning(value = "commentDeletionMethods()")
    public void logCommentDeletion(JoinPoint joinPoint) {
        try {
            Map<String, Object> context = commentContext.get();
            Long commentId = (Long) context.get("commentId");
            String commentContent = (String) context.get("commentContent");

            if (commentId != null) {
                String description = commentContent != null ?
                        String.format("댓글 '%s'가 삭제되었습니다.", activityLogHelper.truncateText(commentContent, 30)) :
                        "댓글이 삭제되었습니다.";

                activityLogHelper.logSimpleActivity(
                        ActionType.DELETE,
                        EntityType.COMMENT,
                        commentId,
                        description
                );

                log.debug("Comment 삭제 로그 기록 완료 - ID: {}", commentId);
            }
        } catch (Exception e) {
            log.error("Comment 삭제 로그 기록 중 오류 발생", e);
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
            // TODO: CommentService.signup(Long id, String detail) 확인 후 정확한 인덱스 조정
            // 현재는 첫 번째 인자가 task ID라고 가정
            if (args.length >= 1 && args[0] instanceof Long) {
                return (Long) args[0];
            }
        } catch (Exception e) {
            log.debug("인자에서 Task ID 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 메서드 인자에서 Comment ID 추출
     */
    private Long extractCommentIdFromArgs(Object[] args) {
        try {
            // TODO: 실제 Comment 수정/삭제 메서드 구현 후 정확한 인자 위치 확인 필요
            if (args.length >= 1 && args[0] instanceof Long) {
                return (Long) args[0];
            }
        } catch (Exception e) {
            log.debug("인자에서 Comment ID 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 결과에서 댓글 내용 추출
     */
    private String extractCommentContentFromResult(Object result) {
        try {
            if (result == null) return null;

            // TODO: CommentResponseDto 구조 확인 후 정확한 필드명으로 수정 필요
            // ApiResponse인 경우 data 필드에서 추출
            var dataField = activityLogHelper.findFieldRecursively(result.getClass(), "data");
            if (dataField != null) {
                dataField.setAccessible(true);
                Object dataValue = dataField.get(result);
                if (dataValue != null) {
                    result = dataValue;
                }
            }

            // detail 필드에서 댓글 내용 추출
            var detailField = activityLogHelper.findFieldRecursively(result.getClass(), "detail");
            if (detailField != null) {
                detailField.setAccessible(true);
                Object value = detailField.get(result);
                return value != null ? value.toString() : null;
            }
        } catch (Exception e) {
            log.debug("결과에서 댓글 내용 추출 실패: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 안전한 ThreadLocal 정리 (메모리 누수 방지)
     */
    private void safeCleanupContext() {
        try {
            Map<String, Object> context = commentContext.get();
            if (context != null) {
                context.clear();
            }
        } catch (Exception e) {
            log.debug("Context 정리 중 오류 발생: {}", e.getMessage());
        } finally {
            try {
                commentContext.remove();
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
            commentContext.remove();
            log.debug("CommentActivityAspect ThreadLocal 정리 완료");
        } catch (Exception e) {
            log.warn("CommentActivityAspect 정리 중 오류 발생: {}", e.getMessage());
        }
    }
}