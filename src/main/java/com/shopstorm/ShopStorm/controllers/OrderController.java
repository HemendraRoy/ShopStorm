package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.dtos.OrderDTO;
import com.shopstorm.ShopStorm.entities.Order;
import com.shopstorm.ShopStorm.entities.OrderStatus;
import com.shopstorm.ShopStorm.mappers.OrderMapper;
import com.shopstorm.ShopStorm.services.OrderService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<Order> placeOrder(@PathVariable Long userId) {
        Order order = orderService.placeOrder(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/buyer/{userId}")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        List<OrderDTO> dtos = orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long orderId,
                                              @RequestParam OrderStatus status,
                                              @RequestParam Long sellerId) {
        Order updated = orderService.updateOrderStatus(orderId, status, sellerId);
        if (updated == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(updated);
    }
}
