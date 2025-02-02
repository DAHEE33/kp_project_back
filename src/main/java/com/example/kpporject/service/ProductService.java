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

    //각 id에 대한 상품 조회
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다 : " + id));
    }
}