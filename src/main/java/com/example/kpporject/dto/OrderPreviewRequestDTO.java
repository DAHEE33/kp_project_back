package com.example.kpporject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//주문 미리보기 요청을 보낼 때 사용
@Getter
@Setter
public class OrderPreviewRequestDTO {
    private Long userId;
    private List<Long> cartIds;
}
