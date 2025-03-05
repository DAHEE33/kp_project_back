package com.example.kpporject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//주문 미리보기 데이터를 반환할 때 사용
@Getter
@Setter
public class OrderPreviewDTO {

    private String username;
    private String phoneNumber;
    private String email;
    private List<OrderItemDTO> orderItems;
    private double totalPrice;
}
