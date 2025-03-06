package com.example.kpporject.controller;

import com.example.kpporject.config.util.JwtTokenProvider;
import com.example.kpporject.dto.OrderPreviewDTO;
import com.example.kpporject.dto.OrderPreviewRequestDTO;
import com.example.kpporject.entity.Order;
import com.example.kpporject.entity.User;
import com.example.kpporject.service.OrderService;
import com.example.kpporject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
     private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * ✅ JWT 토큰에서 userId 가져오는 공통 메서드
     */
    private User validateAndGetUser(String token) {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Unauthorized: 로그인 필요");
        }
        String email = jwtTokenProvider.getUsernameFromJWT(token);
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * ✅ 주문 미리보기 API
     */
    @PostMapping("/preview")
    public ResponseEntity<OrderPreviewDTO> getOrderPreview(
            @CookieValue(value = "loginJwtToken", required = false) String token,
            @RequestBody OrderPreviewRequestDTO request) {
        try {
            // ✅ JWT에서 userId 가져오기
            User user = validateAndGetUser(token);

            // ✅ 주문 미리보기 로직 실행
            OrderPreviewDTO orderPreview = orderService.getOrderPreview(user.getId(), request.getCartIds());
            return ResponseEntity.ok(orderPreview);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * ✅ 주문 생성 API
     */
//    @PostMapping("/create")
//    public ResponseEntity<String> createOrder(
//            @CookieValue(value = "loginJwtToken", required = false) String token,
//            @RequestBody OrderPreviewRequestDTO request) {
//        try {
//            // ✅ JWT에서 userId 가져오기
//            User user = validateAndGetUser(token);
//
//            // ✅ 주문 생성 로직 실행
//            orderService.createOrder(user.getId(), request.getCartIds(), request.getTotalPrice(), request.getDiscountAmount(), request.getPaymentMethod());
//
//            return ResponseEntity.ok("주문이 성공적으로 생성되었습니다.");
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("주문 실패: " + e.getMessage());
//        }
//    }
}
