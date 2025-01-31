package com.example.kpporject.repository;

import com.example.kpporject.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
