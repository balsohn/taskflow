package com.example.taskflow.domain.comment.repository;

import com.example.taskflow.domain.comment.entity.Comment;
import com.example.taskflow.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByTaskIdAndIsDeletedFalse(Long taskId, Pageable pageable);

    Optional<Comment> findByTaskId(Long taskId);

}