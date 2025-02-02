package com.example.kpporject.service;

import com.example.kpporject.entity.Review;
import com.example.kpporject.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // ✅ 상품 ID로 리뷰 조회
    public Page<Review> getReviewsByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable );
    }

    // ✅ 사용자 ID로 리뷰 조회
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
}
