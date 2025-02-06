package com.example.kpporject.service;

import com.example.kpporject.entity.User;
import com.example.kpporject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    public void deleteUser(User user) {userRepository.delete(user);}

    // ✅ 회원 가입
    @Transactional
    public User registerUser(String userName, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User(userName, email); // ✅ 생성자 사용
        return userRepository.save(user);
    }

    // ✅ 회원 조회
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
    }
}
