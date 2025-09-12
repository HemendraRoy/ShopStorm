package com.shopstorm.ShopStorm.repositories;

import com.shopstorm.ShopStorm.entities.Order;
import com.shopstorm.ShopStorm.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer(User buyer);
}
