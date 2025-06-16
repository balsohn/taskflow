package com.example.taskflow.domain.comment.response;

import com.example.taskflow.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentResponse extends JpaRepository<Comment, Long> {


}
