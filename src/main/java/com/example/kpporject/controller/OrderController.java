package com.example.kpporject.controller;

import com.example.kpporject.config.util.JwtTokenProvider;
import com.example.kpporject.dto.OrderPreviewDTO;
import com.example.kpporject.dto.OrderPreviewRequestDTO;
import com.example.kpporject.dto.OrderRequestDto;
import com.example.kpporject.dto.OrderResponseDto;
import com.example.kpporject.entity.Order;
import com.example.kpporject.entity.User;
import com.example.kpporject.service.OrderService;
import com.example.kpporject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @CookieValue(value = "loginJwtToken", required = false) String token,
            @RequestBody OrderRequestDto orderRequest) {

        // 1. JWT 토큰 검증
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 2. JWT에서 username(또는 이메일) 추출 후 사용자 정보 조회
        String email = jwtTokenProvider.getUsernameFromJWT(token);
        Optional<User> optionalUser = userService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
        User user = optionalUser.get();

        // 3. JWT에서 얻은 사용자 정보를 OrderRequestDto에 주입
        orderRequest.setUserId(user.getId());

        // 4. 주문 생성 (임시 주문 저장: PENDING 상태)
        OrderResponseDto response = orderService.createOrder(orderRequest);

        // 5. 생성된 주문 정보를 응답으로 반환 (orderId, 금액, 상태 등)
        return ResponseEntity.ok(response);
    }
}
