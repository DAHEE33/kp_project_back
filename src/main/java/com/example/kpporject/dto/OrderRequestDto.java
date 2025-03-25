package com.example.kpporject.dto;

import lombok.Data;

import java.util.List;

/***
 * 클라이언트 -> 서버에 보낼 데이터의 형태 정의
 */
@Data
public class OrderRequestDto {
    private Long userId;               // 주문한 고객의 고유 ID
    private List<Long> cartIds;        // 주문에 포함된 장바구니 항목의 ID 목록
    private String recipientName;      // 주문자 이름 (배송 정보)
    private String recipientPhone;     // 주문자 전화번호
    private String recipientEmail;     // 주문자 이메일
    private String paymentMethod;      // 결제 방식 (예: "TOSS")
}
