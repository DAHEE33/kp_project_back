package com.example.kpporject.controller;

import com.example.kpporject.config.util.JwtTokenProvider;
import com.example.kpporject.dto.CartDTO;
import com.example.kpporject.entity.Cart;
import com.example.kpporject.entity.Product;
import com.example.kpporject.entity.User;
import com.example.kpporject.service.CartService;
import com.example.kpporject.service.ProductService;
import com.example.kpporject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * ✅ JWT 토큰 검증 및 사용자 조회 (중복 코드 제거)
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
     * ✅ 장바구니 목록 조회 API (쿠키 기반 JWT 인증)
     */
    @GetMapping("/list")
    public ResponseEntity<List<CartDTO>> getCartItems(
            @CookieValue(value = "loginJwtToken", required = false) String token) {

        User user = validateAndGetUser(token);
        List<CartDTO> cartItems = cartService.getCartItemsByUserId(user.getId());

        return ResponseEntity.ok(cartItems);
    }


    /**
     * ✅ 장바구니에 상품 추가 API
     */
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @CookieValue(value = "loginJwtToken", required = false) String token,
            @RequestBody Map<String, Object> payload) {
        try {
            User user = validateAndGetUser(token);
            Long productId = Long.valueOf(payload.get("productId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            cartService.addToCart(user.getId(), productId, quantity);
            return ResponseEntity.ok("상품이 장바구니에 추가되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("장바구니 추가 실패: " + e.getMessage());
        }
    }

    // ✅ 개별 상품 삭제 API
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeItem(
            @CookieValue(value = "loginJwtToken", required = false) String token,
            @PathVariable Long productId) {
        try {
            User user = validateAndGetUser(token);
            cartService.removeFromCart(user.getId(), productId);
            return ResponseEntity.ok("상품이 장바구니에서 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("삭제 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/remove-selected")
    public ResponseEntity<String> removeSelectedItems(
            @CookieValue(value = "loginJwtToken", required = false) String token,
            @RequestBody Map<String, List<Long>> requestData) { // ✅ JSON 객체로 받기
        try {
            User user = validateAndGetUser(token);

            // ✅ JSON 데이터에서 cartIds 추출
            List<Long> cartIds = requestData.get("cartIds");
            System.out.println("📌 요청된 삭제 장바구니 ID 리스트: " + cartIds);
            System.out.println("📌 요청한 사용자 ID: " + user.getId());

            if (cartIds == null || cartIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("삭제할 상품이 없습니다.");
            }

            cartService.removeCartItems(cartIds); // ✅ cartId로 삭제
            return ResponseEntity.ok("선택한 상품이 장바구니에서 삭제되었습니다.");
        } catch (RuntimeException e) {
            System.err.println("❌ 장바구니 삭제 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("선택한 상품 삭제 실패: " + e.getMessage());
        }
    }


    /**
     * ✅ 장바구니 수량 변경 API
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateCartQuantity(
            @CookieValue(value = "loginJwtToken", required = false) String token,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        try {
            User user = validateAndGetUser(token);
            cartService.updateCartQuantity(user.getId(), productId, quantity);
            return ResponseEntity.ok("장바구니 수량이 변경되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("장바구니 수량 변경 실패: " + e.getMessage());
        }
    }

}
