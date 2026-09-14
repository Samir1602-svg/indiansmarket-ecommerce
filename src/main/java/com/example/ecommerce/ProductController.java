package com.example.ecommerce;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public ProductController(ProductRepository productRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public List<Product> getCatalog(@RequestParam(required = false) String category) {
        if (category != null && !category.equalsIgnoreCase("All")) {
            return productRepository.findByCategoryIgnoreCase(category);
        }
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @GetMapping("/{id}/reviews")
    public List<Review> getProductReviews(@PathVariable Long id) {
        return reviewRepository.findByProductIdOrderByReviewDateDesc(id);
    }

    @PostMapping("/{id}/reviews")
    public Review addReview(@PathVariable Long id, @RequestBody Review review) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        review.setProductId(id);
        Review saved = reviewRepository.save(review);

        // Real Average Rating calculation
        List<Review> allReviews = reviewRepository.findByProductIdOrderByReviewDateDesc(id);
        double avg = allReviews.stream().mapToInt(Review::getRatingStars).average().orElse(0.0);
        product.setRating(Math.round(avg * 10.0) / 10.0);
        product.setReviewCount(allReviews.size());
        productRepository.save(product);

        return saved;
    }

    @GetMapping("/{id}/related")
    public List<Product> getRelatedProducts(@PathVariable Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return productRepository.findByCategoryIgnoreCase(p.getCategory()).stream()
                .filter(item -> !item.getId().equals(id))
                .limit(4)
                .toList();
    }

    @GetMapping("/seller/{sellerId}")
    public List<Product> getSellerProducts(@PathVariable Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    @PostMapping("/seller/{sellerId}/add")
    public Product addSellerProduct(@PathVariable Long sellerId, @RequestBody Product product) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));

        product.setSellerId(seller.getId());
        product.setSellerStoreName(seller.getStoreName() != null ? seller.getStoreName() : "Appario Retail");
        product.setSellerState(seller.getSellerState() != null ? seller.getSellerState() : "Delhi");
        product.setSellerTier(seller.getSellerTier() != null ? seller.getSellerTier() : "SILVER");

        // Newly listed item starts with ZERO fake rating
        product.setRating(0.0);
        product.setReviewCount(0);

        if (product.getAsin() == null || product.getAsin().isBlank()) product.setAsin("IM-" + System.currentTimeMillis() % 1000000);
        if (product.getSku() == null || product.getSku().isBlank()) product.setSku("SKU-" + (int)(Math.random() * 90000 + 10000));
        if (product.getOriginalPrice() == null) product.setOriginalPrice(product.getPrice().multiply(java.math.BigDecimal.valueOf(1.25)));

        return productRepository.save(product);
    }
}