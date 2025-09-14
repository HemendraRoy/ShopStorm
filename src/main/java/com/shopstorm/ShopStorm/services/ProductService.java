package com.shopstorm.ShopStorm.services;

import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.entities.Review;
import com.shopstorm.ShopStorm.repositories.ProductRepository;
import com.shopstorm.ShopStorm.repositories.ReviewRepository;
import com.shopstorm.ShopStorm.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final ReviewRepository reviewRepo;

    public ProductService(ProductRepository productRepo,
                          UserRepository userRepo,
                          ReviewRepository reviewRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.reviewRepo = reviewRepo;
    }

    // Add a product with sellerId
    public Product addProduct(Product product, Long sellerId) {
        User seller = userRepo.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        product.setSeller(seller);
        return productRepo.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    public double getAverageRating(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        List<Review> reviews = reviewRepo.findByProduct(product);
        if (reviews.isEmpty()) return 0.0;
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }

    // Update product with sellerId verification
    public Product updateProduct(Long productId, Product updated, Long sellerId) {
        Product existing = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!existing.getSeller().getId().equals(sellerId)) {
            return null; // Forbidden
        }

        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getPrice() != null) existing.setPrice(updated.getPrice());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getStock() != null) existing.setStock(updated.getStock());
        if (updated.getImageUrl() != null) existing.setImageUrl(updated.getImageUrl());

        return productRepo.save(existing);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepo.findByCategory(category);
    }

    public List<Product> getProductsBySellerId(Long sellerId) {
        User seller = userRepo.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return productRepo.findBySeller(seller);
    }

    public List<Product> searchProducts(String keyword) {
        Set<Product> results = new HashSet<>();
        results.addAll(productRepo.findByNameContainingIgnoreCase(keyword));
        results.addAll(productRepo.findByCategoryContainingIgnoreCase(keyword));
        return new ArrayList<>(results);
    }

    public boolean deleteProduct(Long productId, Long sellerId) {
        Product existing = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Verify seller owns this product
        if (!existing.getSeller().getId().equals(sellerId)) {
            return false; // Not allowed
        }

        productRepo.delete(existing);
        return true;
    }

}
