package com.example.kpporject.service;

import com.example.kpporject.entity.Product;
import com.example.kpporject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // 전체 상품 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
