package com.example.kpporject.repository;

import com.example.kpporject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // ✅ 이메일로 사용자 조회
}
