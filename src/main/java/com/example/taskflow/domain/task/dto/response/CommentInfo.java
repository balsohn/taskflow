package com.example.taskflow.domain.task.dto.response;

import com.example.taskflow.domain.comment.entity.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class CommentInfo {

    private final Long id;
    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final UserInfo user;

    public static CommentInfo fromComment(Comment comment) {
        return CommentInfo.builder()
                .id(comment.getId())
                .comment(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .user(UserInfo.fromUser(comment.getUser()))
                .build();
    }
}
