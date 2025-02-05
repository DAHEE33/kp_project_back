package com.example.kpporject.repository;

import com.example.kpporject.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);
    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);
}
