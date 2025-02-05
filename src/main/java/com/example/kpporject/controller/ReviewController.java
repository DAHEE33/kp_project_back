package com.example.kpporject.controller;

import com.example.kpporject.entity.Review;
import com.example.kpporject.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;


    // ✅ 사용자별 리뷰 조회
    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUserId(@PathVariable Long userId) {
        return reviewService.getReviewsByUserId(userId);
    }

    @GetMapping("/{productId}/check-purchase")
    public ResponseEntity<Boolean> checkPurchase(@RequestParam Long userId, @PathVariable Long productId) {
        boolean hasPurchased = reviewService.hasUserPurchasedProduct(userId, productId);
        return ResponseEntity.ok(hasPurchased);
    }

    // ✅ 추천 기능 (유저 ID 추가)
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<?> likeReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        try {
            reviewService.likeReview(reviewId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
