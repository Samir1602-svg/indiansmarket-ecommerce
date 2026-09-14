package com.example.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, ProductRepository productRepository,
                           OrderRepository orderRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats() {
        long sellersCount = userRepository.findAll().stream().filter(u -> "ROLE_SELLER".equals(u.getRole())).count();
        long buyersCount = userRepository.findAll().stream().filter(u -> "ROLE_CUSTOMER".equals(u.getRole())).count();
        long staffCount = userRepository.findAll().stream().filter(u -> "ROLE_STAFF".equals(u.getRole())).count();
        long prodCount = productRepository.count();

        List<User> sellersList = userRepository.findAll().stream()
                .filter(u -> "ROLE_SELLER".equals(u.getRole()))
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalSellers", sellersCount,
                "totalBuyers", buyersCount,
                "totalStaff", staffCount,
                "totalProducts", prodCount,
                "sellersList", sellersList
        ));
    }

    @PostMapping("/staff/create")
    public ResponseEntity<?> createStaff(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Staff email already exists"));
        }

        User staff = new User();
        staff.setEmail(email);
        staff.setPassword(passwordEncoder.encode(req.get("password")));
        staff.setRole("ROLE_STAFF");
        // Store permission in sellerTier or dedicated attribute
        staff.setSellerTier(req.getOrDefault("permission", "CATALOG_ACCESS"));

        userRepository.save(staff);
        return ResponseEntity.ok(Map.of("message", "Staff account created successfully", "permission", staff.getSellerTier()));
    }

    @PostMapping("/seller/{sellerId}/tier")
    public ResponseEntity<?> updateSellerTier(@PathVariable Long sellerId, @RequestParam String tier) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (seller != null) {
            seller.setSellerTier(tier);
            userRepository.save(seller);
            return ResponseEntity.ok(Map.of("message", "Tier updated"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Seller not found"));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delistProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Product delisted"));
    }
}