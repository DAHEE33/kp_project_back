package com.example.kpporject.controller;

import com.example.kpporject.config.util.JwtTokenProvider;
import com.example.kpporject.entity.User;
import com.example.kpporject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * 쿠키에 저장된 JWT 토큰을 이용하여 현재 사용자의 정보를 조회합니다.
     * 쿠키 이름: loginJwtToken
     */
    @GetMapping("/info")
public ResponseEntity<?> getCurrentUser(@CookieValue(value = "loginJwtToken", required = false) String token) {
    System.out.println("✅ 받은 JWT 토큰 User: " + token); // ✅ 디버깅용 로그 추가

    if (token == null || !jwtTokenProvider.validateToken(token)) {
        System.out.println("❌ 토큰이 없거나 유효하지 않음");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("loggedIn", false));
    }

    String email = jwtTokenProvider.getUsernameFromJWT(token);
    System.out.println("✅ 토큰에서 추출한 이메일: " + email);

    Optional<User> optionalUser = userService.getUserByEmail(email);
    if (optionalUser.isEmpty()) {
        System.out.println("❌ 해당 이메일의 유저를 찾을 수 없음: " + email);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("loggedIn", false));
    }

    User user = optionalUser.get();
    System.out.println("✅ 로그인 성공: " + user.getUsername());

    return ResponseEntity.ok(Map.of(
        "loggedIn", true,
        "email", user.getEmail(),
        "username", user.getUsername(),
        "userId", user.getId()
    ));
}


    /**
     * 회원정보 수정
     * */
    @PostMapping("/update")
    public ResponseEntity<?> userUpdate
    (@CookieValue(value = "loginJwtToken", required = false) String token
            ,@RequestBody User paramUser) {
        // 토큰이 없거나 유효하지 않은 경우 401 반환
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            log.info("401 error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        String email = jwtTokenProvider.getUsernameFromJWT(token);

        Optional<User> optionalUser = userService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            log.info("404 user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User existUser = optionalUser.get();
        User saveUser = existUser.toBuilder()
                .username(paramUser.getUsername())
                .phoneNumber(paramUser.getPhoneNumber())
                .updatedAt(LocalDateTime.now())
                .build();
        log.info("update userInfo : {}", saveUser.toString());

        return ResponseEntity.ok(userService.saveUser(saveUser));
    }

    /**
     * 회원탈퇴 처리
     * */
    @GetMapping("/delete")
    public ResponseEntity<?> deleteUser(@CookieValue(value = "loginJwtToken", required = false) String token) {
        // 토큰이 없거나 유효하지 않은 경우 401 반환
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            log.info("401 error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUsernameFromJWT(token);
        Optional<User> optionalUser = userService.getUserByEmail(email);

        if (optionalUser.isEmpty()) {
            log.info("404 user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = optionalUser.get();
        User deleteUser = user.toBuilder().build();
        log.info(deleteUser.toString());
        log.info("delete start");
        userService.deleteUser(deleteUser);

        return ResponseEntity.ok().build();
    }

    // ✅ 회원 가입
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestParam String username, @RequestParam String email) {
        User user = userService.registerUser(username, email);
        return ResponseEntity.ok(user);
    }

    // ✅ 사용자 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
