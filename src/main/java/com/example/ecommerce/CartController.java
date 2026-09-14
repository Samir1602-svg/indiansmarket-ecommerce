package com.example.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartController(CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          ProductRepository productRepository,
                          UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private Cart getOrCreateCart(Long userId) {
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        User user = userRepository.findById(userId).orElse(null);
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        return cartRepository.save(cart);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        Cart cart = getOrCreateCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addToCart(@PathVariable Long userId,
                                       @RequestParam Long productId,
                                       @RequestParam(defaultValue = "1") int quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Product not found"));
        }

        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return ResponseEntity.ok(cartRepository.save(cart));
    }

    @PostMapping("/{userId}/update-quantity")
    public ResponseEntity<?> updateQuantity(@PathVariable Long userId,
                                            @RequestParam Long productId,
                                            @RequestParam int delta) {
        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + delta;
            if (newQty <= 0) {
                cart.getItems().remove(item);
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(newQty);
                cartItemRepository.save(item);
            }
            cartRepository.save(cart);
        }

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable Long userId, @PathVariable Long productId) {
        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
            cartRepository.save(cart);
        }

        return ResponseEntity.ok(Map.of("message", "Item removed successfully"));
    }

    @GetMapping("/{userId}/total")
    public ResponseEntity<?> getCartTotal(@PathVariable Long userId) {
        Cart cart = getOrCreateCart(userId);
        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            if (item.getProduct() != null) {
                total += (item.getQuantity() * item.getProduct().getPrice());
            }
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("totalAmount", total);
        return ResponseEntity.ok(resp);
    }
}