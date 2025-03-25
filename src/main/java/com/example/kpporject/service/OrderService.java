package com.example.kpporject.service;

import com.example.kpporject.dto.OrderItemDTO;
import com.example.kpporject.dto.OrderPreviewDTO;
import com.example.kpporject.dto.OrderRequestDto;
import com.example.kpporject.dto.OrderResponseDto;
import com.example.kpporject.entity.*;
import com.example.kpporject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    /**
     * 20250325 신규
     */
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequest) {
        // 1. 주문한 고객 조회 (회원)
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // 2. 선택된 장바구니 항목 조회
        List<Cart> carts = cartRepository.findByIdInAndUserId(orderRequest.getCartIds(), user.getId());
        if (carts.isEmpty()) {
            throw new RuntimeException("선택된 장바구니 항목이 없습니다.");
        }

        // 3. 주문 금액, 할인 금액 계산 (예시)
        double totalPrice = 0;
//        double discountAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cart : carts) {
            Product product = cart.getProduct();
            int quantity = cart.getQuantity();
            double price = product.getPrice();  // 주문 당시 가격
            double itemTotal = price * quantity;
            totalPrice += itemTotal;

            // 할인 금액 계산은 비즈니스 로직에 따라 결정 (여기서는 예시로 0)
            // discountAmount += ...;

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(price)
                    .totalPrice(itemTotal)
                    .build();
            orderItems.add(orderItem);
        }

        // 4. Order 엔티티 생성 (빌더 패턴 사용)
        Order order = Order.builder()
                .user(user)
                .orderItems(orderItems)
                .totalPrice(totalPrice)
//                .discountAmount(discountAmount)
                .paymentMethod(orderRequest.getPaymentMethod()) // 예: "TOSS"
                .orderStatus("PENDING") // 결제 대기 상태
                .recipientName(orderRequest.getRecipientName())
                .recipientPhone(orderRequest.getRecipientPhone())
                .recipientEmail(orderRequest.getRecipientEmail())
                .build();

        // OrderItem에 order 연관관계 설정 (양방향 연관관계 처리)
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        // 5. 주문 정보를 DB에 저장 (Cascade 옵션으로 orderItems도 함께 저장됨)
        Order savedOrder = orderRepository.save(order);

        // 6. 장바구니 비우기 (주문한 항목은 삭제)
        cartRepository.deleteAll(carts);

        // 7. 응답 DTO 생성 후 반환
        return new OrderResponseDto(savedOrder.getId(), savedOrder.getTotalPrice(),
                savedOrder.getDiscountAmount(), savedOrder.getOrderStatus());
    }



    ////////////////////////////////////////////////////////////필요없으면 하단 지워도 됨
    @Transactional
    public Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities, String paymentMethod) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        double totalPrice = 0;
        double discountAmount = 0; // 할인 로직 필요

        Order order = Order.builder()
                .user(user)
                .paymentMethod(paymentMethod)
                .orderStatus("결제 대기")
                .recipientName(user.getUsername())
                .recipientPhone(user.getPhoneNumber())
                .recipientEmail(user.getEmail())
                .totalPrice(totalPrice)
                .discountAmount(discountAmount)
                .build();

        order = orderRepository.save(order);

        for (int i = 0; i < productIds.size(); i++) {
            Product product = productRepository.findById(productIds.get(i)).orElseThrow(() -> new RuntimeException("Product not found"));
            int quantity = quantities.get(i);
            double price = product.getPrice();
            double totalItemPrice = price * quantity;

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .price(price)
                    .totalPrice(totalItemPrice)
                    .build();

            orderItemRepository.save(orderItem);
            totalPrice += totalItemPrice;
        }

        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public OrderPreviewDTO getOrderPreview(Long userId, List<Long> cartIds) {
        List<Cart> selectedCarts = cartRepository.findByIdInAndUserId(cartIds, userId);

        if (selectedCarts.isEmpty()) {
            throw new RuntimeException("선택된 장바구니 상품이 없습니다.");
        }

        OrderPreviewDTO orderPreview = new OrderPreviewDTO();
        orderPreview.setUsername(selectedCarts.get(0).getUser().getUsername());
        orderPreview.setPhoneNumber(selectedCarts.get(0).getUser().getPhoneNumber() == null ? "" : selectedCarts.get(0).getUser().getPhoneNumber());
        orderPreview.setEmail(selectedCarts.get(0).getUser().getEmail());

        List<OrderItemDTO> orderItems = selectedCarts.stream().map(cart -> {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setProductName(cart.getProduct().getName());
            itemDto.setImageUrl(cart.getProduct().getImageUrl());
            itemDto.setQuantity(cart.getQuantity());
            itemDto.setPrice(cart.getProduct().getPrice());
            itemDto.setTotalPrice(cart.getQuantity() * cart.getProduct().getPrice());
            return itemDto;
        }).collect(Collectors.toList());

        orderPreview.setOrderItems(orderItems);
        orderPreview.setTotalPrice(orderItems.stream().mapToDouble(OrderItemDTO::getTotalPrice).sum());

        return orderPreview;
    }

}
