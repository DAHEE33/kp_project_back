package com.example.kpporject.dto;

import com.example.kpporject.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private double price;
    private int quantity;

     public CartDTO(Cart cart) {
        this.id = cart.getId();
        this.productId = cart.getProduct().getId();
        this.productName = cart.getProduct().getName();
        this.imageUrl = cart.getProduct().getImageUrl(); // ✅ imageUrl을 DB에서 가져옴
        this.price = cart.getProduct().getPrice();
        this.quantity = cart.getQuantity();
    }
}
