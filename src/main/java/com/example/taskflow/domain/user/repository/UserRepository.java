package com.example.taskflow.domain.user.repository;

import com.example.taskflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {

    // username 기반 조회
    Optional<User> findByUsername(String username);

    default User findByUsernameOrElseThrow(String username){
       return findByUsername(username).orElseThrow(()->new IllegalArgumentException("잘못된 사용자명 또는 비밀번호입니다"));
    }

    // id 기반 조회
    Optional<User> findById(Long id);

    default User findByIdOrElseThrow(Long id){
        return findById(id).orElseThrow(()-> new IllegalArgumentException("유저를 찾지 못했습니다."));
    }

    // username 중복 검증
    boolean existsByUsername(String username);

    // email 중복 검증
    boolean existsByemail(String email);

    User findByName(String name);
}
