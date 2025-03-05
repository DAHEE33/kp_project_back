package com.example.kpporject.controller;

import com.example.kpporject.dto.OrderPreviewDTO;
import com.example.kpporject.dto.OrderPreviewRequestDTO;
import com.example.kpporject.entity.Order;
import com.example.kpporject.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
     private final OrderService orderService;

     // ✅ 주문 미리보기 API 추가
    @PostMapping("/preview")
    public ResponseEntity<OrderPreviewDTO> getOrderPreview(
            @RequestBody OrderPreviewRequestDTO request) {
        try {
            OrderPreviewDTO orderPreview = orderService.getOrderPreview(request.getUserId(), request.getCartIds());
            return ResponseEntity.ok(orderPreview);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Order> createOrder(
            @PathVariable Long userId,
            @RequestParam List<Long> productIds,
            @RequestParam List<Integer> quantities,
            @RequestParam String paymentMethod) {
        return ResponseEntity.ok(orderService.createOrder(userId, productIds, quantities, paymentMethod));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }
}
