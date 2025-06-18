package com.example.taskflow.domain.comment.response;

import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentResponse extends JpaRepository<Comment, Long> {

    List<Comment> UserAndComment(User user, String comment);

}
