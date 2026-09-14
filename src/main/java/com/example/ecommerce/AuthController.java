package com.example.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String password = req.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
        }

        // 1. AUTO-SEED SUPER ADMIN (Agar database clean restart hone par owner miss ho gaya ho)
        if ("owner@indiansmarket.in".equalsIgnoreCase(email)) {
            User owner = userRepository.findByEmail("owner@indiansmarket.in").orElse(null);
            if (owner == null) {
                owner = new User();
                owner.setEmail("owner@indiansmarket.in");
                owner.setPassword(passwordEncoder.encode("Carelon16sep"));
                owner.setRole("ROLE_ADMIN");
                owner.setSellerTier("FULL_CONTROL");
                userRepository.save(owner);
            }
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }

        // 2. DUAL MATCH (BCrypt Hash match YA Plain-Text legacy match)
        boolean isMatch = false;
        try {
            isMatch = passwordEncoder.matches(password, user.getPassword());
        } catch (Exception ignored) {}

        if (!isMatch && password.equals(user.getPassword())) {
            isMatch = true;
            // Upgrade legacy plain-text password to BCrypt in DB
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }

        if (!isMatch) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }

        String role = user.getRole() != null ? user.getRole() : "ROLE_CUSTOMER";
        String token = jwtUtil.generateToken(user.getEmail(), role);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", role,
                "email", user.getEmail(),
                "userId", user.getId(),
                "permission", user.getSellerTier() != null ? user.getSellerTier() : "FULL_CONTROL"
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use"));
        }

        User u = new User();
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(req.get("password")));
        String type = req.getOrDefault("accountType", "CUSTOMER");
        if ("SELLER".equalsIgnoreCase(type)) {
            u.setRole("ROLE_SELLER");
            u.setSellerTier("SILVER");
            u.setStoreName(req.getOrDefault("storeName", "Merchant Store"));
        } else {
            u.setRole("ROLE_CUSTOMER");
        }

        User saved = userRepository.save(u);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole());
        return ResponseEntity.ok(Map.of("token", token, "role", saved.getRole(), "email", saved.getEmail(), "userId", saved.getId()));
    }
}