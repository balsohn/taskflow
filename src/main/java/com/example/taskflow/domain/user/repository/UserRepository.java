package com.example.taskflow.domain.user.repository;

import com.example.taskflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    default User findByUsernameOrElseThrow(String username){
        return findByUsername(username).orElseThrow(()->new IllegalArgumentException("아이디를 찾지 못했습니다"));
    }

    User findByUserId(Long userId);

    List<User> findByName(String name);

    // username 중복 검증
    boolean existsByUsername(String username);

    // email 중복 검증
    boolean existsByemail(String email);

}
