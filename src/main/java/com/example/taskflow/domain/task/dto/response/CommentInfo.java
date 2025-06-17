package com.example.taskflow.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentInfo {

    private final Long id;
    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final UserInfo user;

    public static CommentInfo fromComment(Comment comment) {
        return CommentInfo.builder().id(comment.getId()).comment(comment.getComment()).createdAtAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt()).user(UserInfo.fromUser(comment.getUser())).build();
    }
}
