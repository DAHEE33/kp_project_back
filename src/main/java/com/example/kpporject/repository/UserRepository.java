package com.example.kpporject.repository;

import com.example.kpporject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일(userId)로 사용자 조회
    Optional<User> findByEmail(String email);
}
