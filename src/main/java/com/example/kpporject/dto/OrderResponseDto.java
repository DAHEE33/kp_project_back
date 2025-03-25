package com.example.kpporject.dto;

import lombok.Data;

/**
 * 서버 - > 클라이언트 반환할 데이터
 * 토스 페이먼츠에서 사용될 값들을 고려하여 각각 다르게 만듦
 */
@Data
public class OrderResponseDto {
    private Long orderId;
    private double totalPrice;
    private double discountAmount;
    private String orderStatus;

    public OrderResponseDto(Long orderId, double totalPrice, double discountAmount, String orderStatus) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.discountAmount = discountAmount;
        this.orderStatus = orderStatus;
    }
}
