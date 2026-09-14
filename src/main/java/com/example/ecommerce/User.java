package com.example.ecommerce;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ROLE_CUSTOMER, ROLE_SELLER, ROLE_ADMIN, ROLE_STAFF

    // Seller Marketplace Attributes
    private String storeName;
    private String sellerCity;
    private String sellerState;
    private String sellerTier; // SILVER, GOLD, DIAMOND
    private Double sellerRating;

    // Internal Ops Staff Permissions
    private String staffPermission; // CATALOG_ACCESS, DISPUTE_ACCESS, FULL_CONTROL

    // Default No-Args Constructor (JPA ke liye zaroori)
    public User() {
        this.sellerTier = "SILVER";
        this.sellerRating = 4.2;
    }

    // 4-argument constructor (AuthController ke liye)
    public User(String email, String password, String role, String storeName) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.storeName = storeName;
        this.sellerTier = "SILVER";
        this.sellerRating = 4.2;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getSellerCity() { return sellerCity; }
    public void setSellerCity(String sellerCity) { this.sellerCity = sellerCity; }
    public String getSellerState() { return sellerState; }
    public void setSellerState(String sellerState) { this.sellerState = sellerState; }
    public String getSellerTier() { return sellerTier; }
    public void setSellerTier(String sellerTier) { this.sellerTier = sellerTier; }
    public Double getSellerRating() { return sellerRating; }
    public void setSellerRating(Double sellerRating) { this.sellerRating = sellerRating; }
    public String getStaffPermission() { return staffPermission; }
    public void setStaffPermission(String staffPermission) { this.staffPermission = staffPermission; }
}