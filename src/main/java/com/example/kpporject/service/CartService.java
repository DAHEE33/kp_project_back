package com.example.kpporject.service;

import com.example.kpporject.dto.CartDTO;
import com.example.kpporject.entity.Cart;
import com.example.kpporject.entity.Product;
import com.example.kpporject.entity.User;
import com.example.kpporject.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;

    // ✅ 사용자 장바구니 조회
    public List<CartDTO> getCartItemsByUserId(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserIdWithProduct(userId); // ✅ JOIN FETCH 사용
        return cartItems.stream()
                .map(cart -> new CartDTO(
                        cart.getId(),
                        cart.getProduct().getId(),
                        cart.getProduct().getName(),
                        cart.getProduct().getImageUrl(),
                        cart.getProduct().getPrice(),  // 🚨 여기가 문제 발생
                        cart.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    // ✅ 장바구니 추가 (수정된 부분)
    @Transactional
    public void addToCart(Long userId, Long productId, int quantity) {
        // ✅ 상품이 실제 존재하는지 확인
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("❌ 상품을 찾을 수 없습니다. productId: " + productId);
        }

        Cart existingCartItem = cartRepository.findByUserIdAndProductId(userId, productId);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartRepository.save(existingCartItem);
        } else {
            User user = userService.getUserById(userId);

            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(quantity);

            cartRepository.save(cart);
        }
    }

    // ✅ 장바구니에서 삭제 (수정된 부분)
    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new RuntimeException("장바구니에 해당 상품이 없습니다.");
        }
        cartRepository.delete(cart);
    }

    // ✅ 장바구니 수량 업데이트 (수정된 부분)
    @Transactional
    public void updateCartQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new RuntimeException("장바구니에 해당 상품이 없습니다.");
        }
        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }
}
