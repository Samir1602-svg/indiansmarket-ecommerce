package com.example.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public CartController(CartRepository cartRepository, ProductRepository productRepository,
                          UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    private User resolveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));
    }

    private Cart getOrCreateCart(Long userId) {
        User user = resolveUser(userId);
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(getOrCreateCart(userId));
    }

    @PostMapping("/{userId}/add")
    @Transactional
    public ResponseEntity<Cart> addToCart(@PathVariable Long userId,
                                          @RequestParam Long productId,
                                          @RequestParam(defaultValue = "1") Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cart.getItems().add(newItem);
        }

        return ResponseEntity.ok(cartRepository.save(cart));
    }

    // UPDATE QUANTITY (+ / -)
    @PostMapping("/{userId}/update-quantity")
    @Transactional
    public ResponseEntity<Cart> updateQuantity(@PathVariable Long userId,
                                               @RequestParam Long productId,
                                               @RequestParam Integer delta) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            int newQty = item.getQuantity() + delta;
            if (newQty <= 0) {
                cart.getItems().remove(item);
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(newQty);
            }
            cartRepository.save(cart);
        }
        return ResponseEntity.ok(cart);
    }

    // REMOVE ITEM FROM CART
    @DeleteMapping("/{userId}/remove/{productId}")
    @Transactional
    public ResponseEntity<Cart> removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
            cartRepository.save(cart);
        }
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/{userId}/total")
    public ResponseEntity<Map<String, BigDecimal>> getCartTotal(@PathVariable Long userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems().isEmpty()) {
            return ResponseEntity.ok(Map.of("totalAmount", BigDecimal.ZERO));
        }

        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(Map.of("totalAmount", total));
    }
}