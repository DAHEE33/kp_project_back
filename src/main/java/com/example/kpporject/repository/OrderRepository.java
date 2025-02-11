package com.example.kpporject.repository;

import com.example.kpporject.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ 특정 사용자가 특정 상품을 구매한 적이 있는지 확인
    @Query("SELECT COUNT(o) > 0 FROM Order o " +
            "JOIN o.orderItems oi " +
            "WHERE o.user.id = :userId AND oi.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

}
