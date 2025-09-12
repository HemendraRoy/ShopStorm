package com.shopstorm.ShopStorm.repositories;

import com.shopstorm.ShopStorm.entities.Review;
import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
    Optional<Review> findByProductAndUser(Product product, User user);
}
