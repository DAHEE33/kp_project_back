package com.example.kpporject.controller;

import com.example.kpporject.dto.ProductDTO;
import com.example.kpporject.entity.Product;
import com.example.kpporject.entity.Review;
import com.example.kpporject.service.ProductService;
import com.example.kpporject.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pass/products") // pass : 개발용 security filter 통과 uri
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    // ✅ 전체 상품 목록 조회 -- 모든 회원
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts().stream()
                .map(ProductDTO::new)  // ✅ Product → ProductDTO로 변환
                .toList();
        return ResponseEntity.ok(products);
    }

    // ✅ 특정 상품 상세 정보 조회 -- 모든 회원
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(new ProductDTO(product));
    }

    // ✅ 리뷰 조회 (정렬 기능 포함) -- 모든 회원
    @GetMapping("/{id}/reviews")
    public ResponseEntity<Page<Review>> getReviewsByProductId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(id, page, size, sort));
    }


}
