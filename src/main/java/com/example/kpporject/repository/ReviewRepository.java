package com.example.kpporject.repository;

import com.example.kpporject.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductId(Long productId, Pageable pageable);  // ✅ 상품 기준 리뷰 조회
    List<Review> findByUserId(Long userId);        // ✅ 사용자 기준 리뷰 조회

    boolean existsByUserIdAndProductId(Long userId, Long productId);

}
