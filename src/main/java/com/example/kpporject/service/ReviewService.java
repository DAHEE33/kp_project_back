package com.example.kpporject.service;

import com.example.kpporject.entity.Product;
import com.example.kpporject.entity.Review;
import com.example.kpporject.entity.ReviewLike;
import com.example.kpporject.entity.User;
import com.example.kpporject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ✅ 리뷰 목록 조회 (복합 정렬 로직 추가)
    public Page<Review> getReviewsByProductId(Long productId, int page, int size, String sortOption) {
        Sort sort;

        switch (sortOption) {
            case "rating":  // ⭐ 별점순 → 추천순 → 최신순
                sort = Sort.by(
                        Sort.Order.desc("rating"),
                        Sort.Order.desc("likes"),
                        Sort.Order.desc("createdAt")
                );
                break;

            case "likes":   // 👍 추천순 → 별점순 → 최신순
                sort = Sort.by(
                        Sort.Order.desc("likes"),
                        Sort.Order.desc("rating"),
                        Sort.Order.desc("createdAt")
                );
                break;

            default:        // 🆕 최신순 → 별점순 → 추천순 (기본값)
                sort = Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("rating"),
                        Sort.Order.desc("likes")
                );
                break;
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return reviewRepository.findByProductId(productId, pageable);
    }

    // ✅ 리뷰 추천 (중복 방지 로직 추가)
    public ResponseEntity<?> likeReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // ✅ 중복 추천 방지 (에러 대신 200 응답과 메시지만 반환)
        if (reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            return ResponseEntity.ok("이미 추천한 리뷰입니다."); // 🔹 HTTP 200 응답으로 변경
        }

        // 추천 처리
        review.setLikes(review.getLikes() + 1);
        reviewRepository.save(review);

        // 추천 기록 저장
        ReviewLike like = new ReviewLike();
        like.setUser(user);
        like.setReview(review);
        reviewLikeRepository.save(like);

        return ResponseEntity.ok("추천이 완료되었습니다."); // 🔹 정상 응답
    }


    // ✅ 사용자 ID로 리뷰 조회
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }


     // ✅ 특정 사용자가 특정 상품을 구매했는지 확인
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndProductId(userId, productId);
    }

    // ✅ 리뷰 작성 가능 여부 확인 (구매 여부 + 리뷰 작성 여부 체크)
    public boolean canUserWriteReview(Long userId, Long productId) {
    // 구매 여부 확인
    boolean hasPurchased = hasUserPurchasedProduct(userId, productId);
    System.out.println("✅ hasPurchased: " + hasPurchased);

    if (!hasPurchased) {
        return false;
    }

    // 기존 리뷰 작성 여부 확인
    boolean hasReview = reviewRepository.existsByUserIdAndProductId(userId, productId);
    System.out.println("✅ hasReview: " + hasReview);

    return !hasReview;
}


    // ✅ 리뷰 등록
      @Transactional
    public Review addReview(Long userId, Long productId, int rating, String comment) {
        // 1️⃣ 구매 여부 확인
        if (!hasUserPurchasedProduct(userId, productId)) {
            throw new RuntimeException("구매한 사용자만 리뷰를 작성할 수 있습니다.");
        }

        // 2️⃣ 리뷰 추가 로직
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        Review review = new Review(user, product, rating, comment);
        return reviewRepository.save(review);
    }



}
