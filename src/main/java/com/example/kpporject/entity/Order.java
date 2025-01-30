package com.example.kpporject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders") // order 대신 orders로 변경
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
}
