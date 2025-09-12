package com.shopstorm.ShopStorm.repositories;

import com.shopstorm.ShopStorm.entities.CartItem;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}
