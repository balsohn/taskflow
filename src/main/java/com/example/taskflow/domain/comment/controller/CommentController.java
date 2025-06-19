package com.example.taskflow.domain.comment.controller;

import com.example.taskflow.domain.comment.dto.CommentDeleteResPonsserDto;
import com.example.taskflow.domain.comment.dto.CommentRequestDto;
import com.example.taskflow.domain.comment.dto.CommentResponseDto;
import com.example.taskflow.domain.comment.dto.findUserNameResponseDto;
import com.example.taskflow.domain.comment.service.CommentService;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/tasks")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> signup
            (@PathVariable Long taskId,
             @Valid @RequestBody CommentRequestDto commentRequestDto,
             @AuthenticationPrincipal User user
            ){
        String username = user.getUsername();

        CommentResponseDto commentResponseDto = commentService.signUp(taskId,username,commentRequestDto.getContent());

        return new ResponseEntity<>(ApiResponse.success("댓글이 생성되었습니다.",
                commentResponseDto), HttpStatus.OK);

    }
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<ApiResponse<PageResponse<findUserNameResponseDto>>> findUserName
            (@PathVariable Long taskId,
             Pageable pageable,
             @RequestParam String content) {
        PageResponse<findUserNameResponseDto> responseDtoList =
                commentService.findUserNameList(content,taskId,pageable);

        return new ResponseEntity<>(ApiResponse.success("댓글 목록을 조회했습니다.",responseDtoList),HttpStatus.OK);

    }

    @DeleteMapping("/{commentId}/comments")
    public ResponseEntity<ApiResponse<CommentDeleteResPonsserDto>>deleteComment(@PathVariable Long commentId) {

        CommentDeleteResPonsserDto commentDeleteResPonsserDto =
                commentService.deleteComment(commentId);

        return new ResponseEntity<>(ApiResponse.success("댓글이 삭제되었습니다.",commentDeleteResPonsserDto),HttpStatus.OK);

    }
}
