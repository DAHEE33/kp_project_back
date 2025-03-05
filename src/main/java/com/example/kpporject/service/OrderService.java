package com.example.kpporject.service;

import com.example.kpporject.dto.OrderItemDTO;
import com.example.kpporject.dto.OrderPreviewDTO;
import com.example.kpporject.entity.*;
import com.example.kpporject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
