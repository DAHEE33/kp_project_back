package com.example.kpporject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    private String imageUrl;

    @Column(nullable = false)
    private double price;

    @Column(nullable = true) // ✅ stock이 NULL 가능
    private Integer stock;  // ✅ int → Integer (null 허용)

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public Product(String name, String description, double price, String imageUrl, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.createdAt = LocalDateTime.now();
    }
}
