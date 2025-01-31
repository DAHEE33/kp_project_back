package com.example.kpporject.service;

import com.example.kpporject.entity.Product;
import com.example.kpporject.entity.Review;
import com.example.kpporject.entity.User;
import com.example.kpporject.repository.ProductRepository;
import com.example.kpporject.repository.ReviewRepository;
import com.example.kpporject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 특정 상품의 리뷰 조회
    public List<Review> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        return reviewRepository.findByProduct(product);
    }

    // 특정 사용자의 리뷰 조회
    public List<Review> getReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        return reviewRepository.findByUser(user);
    }

    // 리뷰 작성
    @Transactional
    public Review createReview(Long userId, Long productId, int rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
