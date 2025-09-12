package com.shopstorm.ShopStorm.repositories;

import com.shopstorm.ShopStorm.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> { }
