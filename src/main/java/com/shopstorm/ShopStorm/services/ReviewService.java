package com.shopstorm.ShopStorm.services;

import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.entities.Review;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.repositories.ProductRepository;
import com.shopstorm.ShopStorm.repositories.ReviewRepository;
import com.shopstorm.ShopStorm.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public ReviewService(ReviewRepository repo, ProductRepository productRepo, UserRepository userRepo) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public Review addReview(Long userId, Long productId, int rating, String comment) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if (repo.findByProductAndUser(product, user).isPresent())
            throw new RuntimeException("User has already reviewed this product");

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        return repo.save(review);
    }

    public List<Review> getReviewsForProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return repo.findByProduct(product);
    }
}
