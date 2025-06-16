package com.example.taskflow.domain.comment.controller;

import com.example.taskflow.domain.comment.dto.CommentRequestDto;
import com.example.taskflow.domain.comment.dto.CommentResponseDto;
import com.example.taskflow.domain.comment.service.CommentService;
import com.example.taskflow.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/tasks")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{takeId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> signup(@PathVariable Long takeId, @Valid @RequestBody
                                                                  CommentRequestDto commentRequestDto){
        return new ResponseEntity<>(ApiResponse.success("댓글이 생성되었습니다.",
                commentService.singup(takeId,commentRequestDto.getDetail())), HttpStatus.OK);

    }

}
