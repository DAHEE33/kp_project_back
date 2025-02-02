package com.example.kpporject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(nullable = true) // ✅ fake_price Null 허용 가능 (선택 사항)
    private Double fake_price;

    @Column(nullable = true) // ✅ options Null 허용
    private Integer options;

    @Column(nullable = true) // ✅ stock이 NULL 가능
    private Integer stock;  // ✅ int → Integer (null 허용)

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonManagedReference  // ✅ 순환 참조 방지
    private List<Review> reviews;

    public Product(String name, String description, double price, String imageUrl, Integer stock, double fakePrice, Integer options) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;  // null 허용
        this.stock = stock;
        this.fake_price = fakePrice;  // 가짜 가격 추가
        this.options = options;      // 옵션 추가
        this.createdAt = LocalDateTime.now();
    }
}
