package com.example.kpporject.controller;

import com.example.kpporject.config.util.JwtTokenProvider;
import com.example.kpporject.entity.Review;
import com.example.kpporject.entity.User;
import com.example.kpporject.repository.ReviewLikeRepository;
import com.example.kpporject.service.ReviewService;
import com.example.kpporject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pass/reviews") // pass : 개발용 security filter 통과 uri
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewLikeRepository reviewLikeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    // ✅ 사용자별 리뷰 조회
    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUserId(@PathVariable Long userId) {
        return reviewService.getReviewsByUserId(userId);
    }

    // ✅ 구매 여부 확인 (리뷰 작성 가능한지 확인)
    @GetMapping("/{productId}/check-purchase")
    public ResponseEntity<Boolean> checkPurchase(
            @CookieValue(value = "loginJwtToken", required = false) String token,
            @PathVariable Long productId) {

        // 토큰 검증
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        // JWT에서 userId 추출
        String email = jwtTokenProvider.getUsernameFromJWT(token);
        Optional<User> optionalUser = userService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

        Long userId = optionalUser.get().getId();
        boolean canWriteReview = reviewService.canUserWriteReview(userId, productId);
        return ResponseEntity.ok(canWriteReview);
    }


    // ✅ 리뷰 작성 가능 여부 확인 API
//    @GetMapping("/{productId}/check-purchase")
//    public ResponseEntity<Boolean> checkPurchase(@RequestParam Long userId, @PathVariable Long productId) {
//        boolean canWriteReview = reviewService.canUserWriteReview(userId, productId);
//        return ResponseEntity.ok(canWriteReview);
//    }

    // ✅ 추천 기능 (좋아요)
//    @PostMapping("/{reviewId}/like")
//    public ResponseEntity<?> likeReview(@PathVariable Long reviewId, @RequestBody Map<String, Long> payload) {
//        try {
//            Long userId = payload.get("userId");
//            reviewService.likeReview(reviewId, userId);
//            return ResponseEntity.ok().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<String> likeReview(
            @PathVariable Long reviewId,
            @CookieValue(value = "loginJwtToken", required = false) String token) {
        try {
            System.out.println("✅ 받은 JWT 토큰 like: " + token);

            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            String email = jwtTokenProvider.getUsernameFromJWT(token);
            Optional<User> optionalUser = userService.getUserByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }

            Long userId = optionalUser.get().getId();

            // ✅ 중복 추천 방지 (HTTP 200으로 메시지만 반환)
            if (reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)) {
                return ResponseEntity.ok("이미 추천한 리뷰입니다.");
            }

            // 추천 처리
            reviewService.likeReview(reviewId, userId);
            return ResponseEntity.ok("추천이 완료되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("추천을 처리하는 중 오류가 발생했습니다.");
        }
    }


    // ✅ 리뷰 등록 (추가된 부분)
//    @PostMapping("/add")
//    public ResponseEntity<Review> addReview(@RequestBody Map<String, Object> payload) {
//        try {
//            Long userId = Long.valueOf(payload.get("userId").toString());
//            Long productId = Long.valueOf(payload.get("productId").toString());
//            int rating = (int) payload.get("rating");
//            String comment = payload.get("comment").toString(); // 긴 텍스트가 있기 때문에 body에 담아서 보낸다.
//
//            // 서비스 메서드 호출로 리뷰 생성
//            Review savedReview = reviewService.addReview(userId, productId, rating, comment);
//            return ResponseEntity.ok(savedReview); // 저장된 리뷰 반환
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(
            @CookieValue(value = "loginJwtToken", required = false) String token,
            @RequestBody Map<String, Object> payload) {

        // 1️⃣ 토큰 검증
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: 로그인 필요");
        }

        // 2️⃣ JWT에서 사용자 정보 가져오기
        String email = jwtTokenProvider.getUsernameFromJWT(token);
        Optional<User> optionalUser = userService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();
        Long userId = user.getId();
        Long productId = Long.valueOf(payload.get("productId").toString());
        int rating = (int) payload.get("rating");
        String comment = payload.get("comment").toString();

        // 3️⃣ **구매한 사용자만 리뷰 작성 가능하도록 검증**
        if (!reviewService.hasUserPurchasedProduct(userId, productId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("구매한 회원만 리뷰 작성이 가능합니다.");
        }

        // 4️⃣ 리뷰 저장
        Review savedReview = reviewService.addReview(userId, productId, rating, comment);
        return ResponseEntity.ok(savedReview);
    }


}
