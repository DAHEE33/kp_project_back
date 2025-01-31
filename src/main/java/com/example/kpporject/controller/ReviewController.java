package com.example.kpporject.controller;

import com.example.kpporject.entity.Review;
import com.example.kpporject.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 특정 상품의 리뷰 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    // 특정 사용자의 리뷰 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    // 리뷰 작성
    @PostMapping("/")
    public ResponseEntity<Review> createReview(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int rating,
            @RequestParam String comment) {
        return ResponseEntity.ok(reviewService.createReview(userId, productId, rating, comment));
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}
