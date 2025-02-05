package com.example.kpporject.Config;

import com.example.kpporject.entity.Product;
import com.example.kpporject.entity.User;
import com.example.kpporject.repository.ProductRepository;
import com.example.kpporject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class TestDataConfig {

    @Bean
    CommandLineRunner initUserDatabase(UserRepository userRepository) {  // ✅ 이름 변경
        return args -> {
            if (userRepository.findByEmail("test@example.com").isEmpty()) {
                User user = new User();
                user.setUsername("testUser");
                user.setEmail("test@example.com");
                user.setPassword("test123");
                user.setOauthProvider("LOCAL");
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        };
    }

    @Bean
    CommandLineRunner initProductDatabase(ProductRepository productRepository) {  // ✅ 이름 변경
        return args -> {
            System.out.println("🚀 기존 상품 데이터를 삭제합니다.");
            productRepository.deleteAll();  // ✅ 기존 데이터 삭제

            System.out.println("🚀 새로운 상품 데이터를 추가합니다.");
            productRepository.saveAll(List.of(
                    new Product("TEST 상품 1", "상품 1입니다", 10000, "https://via.placeholder.com/150", 10),
                    new Product("TEST 상품 2", "상품 2입니다", 20000, "https://via.placeholder.com/150", 20),
                    new Product("TEST 상품 3", "상품 3입니다", 30000, "https://via.placeholder.com/150", 10),
                    new Product("TEST 상품 4", "상품 4입니다", 40000, "https://via.placeholder.com/150", 30)
            ));
        };
    }
}

