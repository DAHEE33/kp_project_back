package com.example.kpporject.service;

import com.example.kpporject.entity.User;
import com.example.kpporject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원 가입
    @Transactional
    public User registerUser(String userName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setUsername(userName);
        user.setEmail(email);
        user.setPassword(password);
        return userRepository.save(user);
    }

    // 회원 조회
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
    }
}
