package com.shopstorm.ShopStorm.services;

import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.entities.Review;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.repositories.ProductRepository;
import com.shopstorm.ShopStorm.repositories.ReviewRepository;
import com.shopstorm.ShopStorm.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final UserRepository userRepo;
    private final ReviewRepository reviewRepo;

    public ProductService(ProductRepository repo, UserRepository userRepo, ReviewRepository reviewRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.reviewRepo = reviewRepo;
    }

    public Product addProduct(Product product) {
        if (product.getSeller() != null && product.getSeller().getId() != null) {
            User seller = userRepo.findById(product.getSeller().getId())
                    .orElseThrow(() -> new RuntimeException("Seller not found"));
            product.setSeller(seller);
        } else {
            throw new IllegalArgumentException("Seller ID is required");
        }
        return repo.save(product);
    }

    public List<Product> getAllProducts() { return repo.findAll(); }

    public Product getProductById(Long id) { return repo.findById(id).orElse(null); }

    public List<Product> getProductsByCategory(String category) { return repo.findByCategory(category); }

    public List<Product> getProductsBySellerId(Long sellerId) {
        User seller = userRepo.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
        return repo.findBySeller(seller);
    }

    public List<Product> searchProducts(String keyword) {
        Set<Product> results = new HashSet<>();
        results.addAll(repo.findByNameContainingIgnoreCase(keyword));
        results.addAll(repo.findByCategoryContainingIgnoreCase(keyword));
        return new ArrayList<>(results);
    }

    public double getAverageRating(Long productId) {
        Product product = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        List<Review> reviews = reviewRepo.findByProduct(product);
        if (reviews.isEmpty()) return 0.0;
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }

    public Product updateProduct(Long productId, Product updated) {
        Product existing = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getPrice() != null) existing.setPrice(updated.getPrice());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getStock() != null) existing.setStock(updated.getStock());
        if (updated.getImageUrl() != null) existing.setImageUrl(updated.getImageUrl());

        return repo.save(existing);
    }
}
