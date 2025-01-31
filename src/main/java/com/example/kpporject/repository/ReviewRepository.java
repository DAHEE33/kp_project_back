package com.example.kpporject.repository;

import com.example.kpporject.entity.Product;
import com.example.kpporject.entity.Review;
import com.example.kpporject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 상품의 리뷰 목록 조회
    List<Review> findByProduct(Product product);

    // 특정 사용자의 리뷰 목록 조회
    List<Review> findByUser(User user);
}
