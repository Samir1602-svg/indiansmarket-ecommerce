package com.example.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository,
                           CartRepository cartRepository,
                           UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<?> checkout(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        Cart cart = cartOpt.get();
        User user = userRepository.findById(userId).orElse(null);

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("IN-OD-" + System.currentTimeMillis());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("SHIPPED");
        order.setCourierPartner("Amazon Transportation Services (ATS)");
        order.setTrackingNumber("ATS-IN-" + (100000 + new Random().nextInt(900000)));

        order.setShippingName(payload.getOrDefault("shippingName", "Valued Customer"));
        order.setShippingPhone(payload.getOrDefault("shippingPhone", "+91 9876543210"));
        order.setShippingAddress(payload.getOrDefault("shippingAddress", "B-42, Connaught Place"));
        order.setShippingCity(payload.getOrDefault("shippingCity", "New Delhi"));
        order.setShippingPincode(payload.getOrDefault("shippingPincode", "110001"));
        order.setPaymentMethod(payload.getOrDefault("paymentMethod", "COD"));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct() == null) continue;

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal unitPrice = BigDecimal.valueOf(cartItem.getProduct().getPrice());
            orderItem.setPrice(unitPrice);
            totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        // Cart clear after checkout
        cart.getItems().clear();
        cartRepository.save(cart);

        return ResponseEntity.ok(savedOrder);
    }
}