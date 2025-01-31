package com.example.kpporject.controller;

import com.example.kpporject.entity.User;
import com.example.kpporject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
            @RequestParam String userName,
            @RequestParam String email,
            @RequestParam String password) {
        return ResponseEntity.ok(userService.registerUser(userName, email, password));
    }

    // 회원 조회
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
