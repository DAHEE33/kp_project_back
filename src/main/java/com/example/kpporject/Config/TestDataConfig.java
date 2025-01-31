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
    CommandLineRunner initDatabase(UserRepository userRepository) {
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
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.saveAll(List.of(
                        new Product(null, "상품 1", 10000, "https://via.placeholder.com/150"),
                        new Product(null, "상품 2", 20000, "https://via.placeholder.com/150"),
                        new Product(null, "상품 3", 30000, "https://via.placeholder.com/150"),
                        new Product(null, "상품 4", 40000, "https://via.placeholder.com/150")
                ));
            }
        };
    }
}
