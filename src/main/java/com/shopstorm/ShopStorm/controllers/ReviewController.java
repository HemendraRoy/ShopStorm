package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.Review;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.services.ReviewService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) { this.reviewService = reviewService; }

    @PostMapping("/{productId}")
    public ResponseEntity<Review> addReview(@AuthenticationPrincipal User currentUser,
                                            @PathVariable Long productId,
                                            @RequestParam int rating,
                                            @RequestParam(required = false) String comment) {
        Review review = reviewService.addReview(currentUser, productId, rating, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(productId));
    }
}
