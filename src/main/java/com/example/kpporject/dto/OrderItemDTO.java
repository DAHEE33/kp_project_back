package com.example.kpporject.dto;

import lombok.Getter;
import lombok.Setter;

//개별 주문 상품 정보를 담는 DTO
@Getter
@Setter
public class OrderItemDTO {
    private String productName;
    private String imageUrl;
    private int quantity;
    private double price;
    private double totalPrice;
}
