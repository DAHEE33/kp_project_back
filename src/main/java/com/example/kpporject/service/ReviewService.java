package com.example.kpporject.service;

import com.example.kpporject.entity.Review;
import com.example.kpporject.entity.ReviewLike;
import com.example.kpporject.entity.User;
import com.example.kpporject.repository.ReviewLikeRepository;
import com.example.kpporject.repository.ReviewRepository;
import com.example.kpporject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private UserRepository userRepository;


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
    public void likeReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // ✅ 중복 추천 방지
        if (reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            throw new RuntimeException("이미 추천한 리뷰입니다.");
        }

        // 추천 처리
        review.setLikes(review.getLikes() + 1);
        reviewRepository.save(review);

        // 추천 기록 저장
        ReviewLike like = new ReviewLike();
        like.setUser(user);
        like.setReview(review);
        reviewLikeRepository.save(like);
    }

    // ✅ 사용자 ID로 리뷰 조회
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    // ✅ 리뷰 작성 여부 확ㅇ
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }


}
