package com.example.kpporject.repository;

import com.example.kpporject.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

    Cart findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user.id = :userId")
    List<Cart> findByUserIdWithProduct(@Param("userId") Long userId);


    // ✅ 특정 사용자의 여러 상품 삭제 (선택 삭제)
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.id IN :cartIds")
    void deleteByIdIn(@Param("cartIds") List<Long> cartIds);


}
