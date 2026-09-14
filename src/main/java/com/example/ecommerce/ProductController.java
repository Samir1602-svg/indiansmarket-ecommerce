package com.example.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Public catalog for storefront buyers
    @GetMapping
    public List<Product> getAllProducts(@RequestParam(required = false) String category) {
        if (category != null && !category.equalsIgnoreCase("All")) {
            return productRepository.findByCategoryIgnoreCase(category);
        }
        return productRepository.findAll();
    }

    // SELLER INVENTORY: Returns only products belonging to this seller store
    @GetMapping("/seller")
    public List<Product> getProductsBySeller(@RequestParam String sellerStoreName) {
        return productRepository.findAll().stream()
                .filter(p -> p.getSellerStoreName() != null && p.getSellerStoreName().trim().equalsIgnoreCase(sellerStoreName.trim()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/related")
    public List<Product> getRelatedProducts(@PathVariable Long id) {
        return productRepository.findAll().stream()
                .filter(p -> !p.getId().equals(id))
                .limit(4)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }
}