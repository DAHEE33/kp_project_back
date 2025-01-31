package com.example.kpporject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String imageUrl; // 상품 이미지 URL
    private double price;
    private int stock;
    private LocalDateTime createdAt;

    public Product(Object o, String s, int i, String url) {
    }

    public Product() {

    }
}
