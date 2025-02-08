package com.example.kpporject.controller;

import com.example.kpporject.entity.Review;
import com.example.kpporject.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pass/reviews") // pass : 개발용 security filter 통과 uri
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // ✅ 사용자별 리뷰 조회
    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUserId(@PathVariable Long userId) {
        return reviewService.getReviewsByUserId(userId);
    }

    // ✅ 구매 여부 확인 (리뷰 작성 가능한지 확인)
//    @GetMapping("/{productId}/check-purchase")
//    public ResponseEntity<Boolean> checkPurchase(@RequestParam Long userId, @PathVariable Long productId) {
//        boolean hasPurchased = reviewService.hasUserPurchasedProduct(userId, productId);
//        return ResponseEntity.ok(hasPurchased);
//    }

    // ✅ 리뷰 작성 가능 여부 확인 API
    @GetMapping("/{productId}/check-purchase")
    public ResponseEntity<Boolean> checkPurchase(@RequestParam Long userId, @PathVariable Long productId) {
        boolean canWriteReview = reviewService.canUserWriteReview(userId, productId);
        return ResponseEntity.ok(canWriteReview);
    }

    // ✅ 추천 기능 (좋아요)
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<?> likeReview(@PathVariable Long reviewId, @RequestBody Map<String, Long> payload) {
        try {
            Long userId = payload.get("userId");
            reviewService.likeReview(reviewId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ✅ 리뷰 등록 (추가된 부분)
    @PostMapping("/add")
    public ResponseEntity<Review> addReview(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long productId = Long.valueOf(payload.get("productId").toString());
            int rating = (int) payload.get("rating");
            String comment = payload.get("comment").toString(); // 긴 텍스트가 있기 때문에 body에 담아서 보낸다.

            // 서비스 메서드 호출로 리뷰 생성
            Review savedReview = reviewService.addReview(userId, productId, rating, comment);
            return ResponseEntity.ok(savedReview); // 저장된 리뷰 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
