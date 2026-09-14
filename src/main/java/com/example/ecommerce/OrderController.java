package com.example.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, CartRepository cartRepository,
                           CartItemRepository cartItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/checkout/{userId}")
    @Transactional
    public ResponseEntity<?> placeOrder(@PathVariable Long userId, @RequestBody Map<String, String> addressData) {
        User user = userRepository.findById(userId)
                .orElseGet(() -> {
                    String email = addressData.get("userEmail");
                    if (email != null) {
                        return userRepository.findByEmail(email).orElse(null);
                    }
                    return userRepository.findAll().stream().findFirst().orElse(null);
                });

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No valid user account"));
        }

        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            BigDecimal p = ci.getProduct().getPrice() != null ? ci.getProduct().getPrice() : BigDecimal.valueOf(999);
            total = total.add(p.multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        Random random = new Random();
        String orderNum = String.format("402-%07d-%07d", random.nextInt(10000000), random.nextInt(10000000));
        String awbNum = "ATS-IN-" + (10000000 + random.nextInt(90000000));

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(orderNum);
        order.setTotalAmount(total);
        order.setStatus("ORDER_PLACED");
        order.setPaymentMethod(addressData.getOrDefault("paymentMethod", "COD"));
        order.setShippingName(addressData.getOrDefault("shippingName", "Customer"));
        order.setShippingPhone(addressData.getOrDefault("shippingPhone", "9876543210"));
        order.setShippingAddress(addressData.getOrDefault("shippingAddress", "Flat 402, Main Street"));
        order.setShippingCity(addressData.getOrDefault("shippingCity", "New Delhi"));
        order.setShippingPincode(addressData.getOrDefault("shippingPincode", "110001"));
        order.setCourierPartner("Amazon Transportation Services (ATS)");
        order.setTrackingNumber(awbNum);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());
            order.getItems().add(oi);
        }

        Order saved = orderRepository.save(order);

        // CLEAR CART
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);

        return ResponseEntity.ok(Map.of(
                "message", "Order placed successfully!",
                "orderId", saved.getId(),
                "orderNumber", saved.getOrderNumber(),
                "totalAmount", saved.getTotalAmount(),
                "status", saved.getStatus(),
                "trackingNumber", saved.getTrackingNumber(),
                "courierPartner", saved.getCourierPartner()
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> list = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (list.isEmpty()) {
            list = orderRepository.findAllByOrderByCreatedAtDesc();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrdersForLogistics() {
        return ResponseEntity.ok(orderRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping("/admin/{orderId}/update-status")
    public ResponseEntity<?> updateLogisticsStatus(@PathVariable Long orderId, @RequestBody Map<String, String> req) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));

        if (req.containsKey("status")) order.setStatus(req.get("status"));
        if (req.containsKey("courierPartner")) order.setCourierPartner(req.get("courierPartner"));
        if (req.containsKey("trackingNumber")) order.setTrackingNumber(req.get("trackingNumber"));
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
        return ResponseEntity.ok(Map.of("message", "Status updated successfully!", "order", order));
    }
}