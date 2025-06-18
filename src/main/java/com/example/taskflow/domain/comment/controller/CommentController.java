package com.example.taskflow.domain.comment.controller;

import com.example.taskflow.domain.comment.dto.CommentRequestDto;
import com.example.taskflow.domain.comment.dto.CommentResponseDto;
import com.example.taskflow.domain.comment.dto.findUserNameRequestDto;
import com.example.taskflow.domain.comment.dto.findUserNameResponseDto;
import com.example.taskflow.domain.comment.service.CommentService;
import com.example.taskflow.domain.user.entity.User;
import com.example.taskflow.global.common.ApiResponse;
import com.example.taskflow.global.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/tasks")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> signup
            (@PathVariable Long taskId,
             @Valid @RequestBody CommentRequestDto commentRequestDto,
             @AuthenticationPrincipal User User
    ){
        Long userId = User.getId();

        CommentResponseDto commentResponseDto = commentService.singup(taskId,commentRequestDto.getContent());

        return new ResponseEntity<>(ApiResponse.success("댓글이 생성되었습니다.",
                commentResponseDto), HttpStatus.OK);

    }
    @GetMapping()
    public ResponseEntity<ApiResponse<PageResponse<findUserNameResponseDto>>> findUserName
            (@Valid @RequestBody
             findUserNameRequestDto findUserNameRequestDto,
             CommentResponseDto commentResponseDto,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size) {
             PageResponse<findUserNameResponseDto> responseDtoList =
                commentService.findUserNameList(findUserNameRequestDto.getUserName(),
                        commentResponseDto.getContent(),page,size);

             return new ResponseEntity<>(ApiResponse.success("댓글 조회를 성공하였습니다.",responseDtoList),HttpStatus.OK);

    }

}
