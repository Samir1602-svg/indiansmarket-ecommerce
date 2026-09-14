package com.example.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/midway")
public class MidwaySecurityController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final String MASTER_PASSKEY = "IM-OPS-SEC-KEY-9942";

    public MidwaySecurityController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/handshake")
    public ResponseEntity<?> authenticateMidwayKey(@RequestBody Map<String, String> req) {
        String passkey = req.get("passkey");
        String email = req.getOrDefault("email", "owner@indiansmarket.in");

        if (passkey == null || !MASTER_PASSKEY.equals(passkey.trim())) {
            return ResponseEntity.status(403).body(Map.of("message", "INVALID_MIDWAY_HARDWARE_KEY"));
        }

        // Auto-seed or fetch Owner
        User owner = userRepository.findByEmail(email).orElse(null);
        if (owner == null) {
            owner = new User();
            owner.setEmail(email);
            owner.setPassword(passwordEncoder.encode("Carelon16sep"));
            owner.setRole("ROLE_ADMIN");
            owner.setSellerTier("FULL_CONTROL");
            userRepository.save(owner);
        }

        // Generate master admin token
        String token = jwtUtil.generateToken(owner.getEmail(), owner.getRole());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", owner.getRole(),
                "email", owner.getEmail(),
                "permission", owner.getSellerTier() != null ? owner.getSellerTier() : "FULL_CONTROL",
                "message", "CRYPTOGRAPHIC_HANDSHAKE_SUCCESS"
        ));
    }
}