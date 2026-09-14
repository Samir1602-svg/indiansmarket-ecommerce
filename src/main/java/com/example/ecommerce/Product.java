package com.example.ecommerce;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String brand;

    @Column(length = 2500)
    private String description;

    @Column(length = 2000)
    private String specifications; // JSON / Bullet Spec String

    @Column(nullable = false)
    private BigDecimal price;
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private Integer stockQuantity;
    private String category;

    @Column(length = 1500)
    private String imageUrl;
    private String asin;
    private String sku;

    private Double rating;
    private Integer reviewCount;

    @Column(nullable = false)
    private Long sellerId;
    private String sellerStoreName;
    private String sellerState;
    private String sellerTier;

    public Product() {
        this.rating = 0.0;
        this.reviewCount = 0;
        this.sellerTier = "SILVER";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getAsin() { return asin; }
    public void setAsin(String asin) { this.asin = asin; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getSellerStoreName() { return sellerStoreName; }
    public void setSellerStoreName(String sellerStoreName) { this.sellerStoreName = sellerStoreName; }
    public String getSellerState() { return sellerState; }
    public void setSellerState(String sellerState) { this.sellerState = sellerState; }
    public String getSellerTier() { return sellerTier; }
    public void setSellerTier(String sellerTier) { this.sellerTier = sellerTier; }
}