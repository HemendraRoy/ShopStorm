package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.Order;
import com.shopstorm.ShopStorm.entities.OrderItem;
import com.shopstorm.ShopStorm.entities.OrderStatus;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.services.OrderService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping("/checkout")
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal User currentUser) {
        Order order = orderService.placeOrder(currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/buyer")
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(orderService.getOrdersByUser(currentUser));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@AuthenticationPrincipal User currentUser,
                                          @PathVariable Long orderId) {
        Order order = orderService.getOrderById(currentUser, orderId);
        return (order != null) ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long orderId,
                                              @RequestParam OrderStatus status,
                                              @AuthenticationPrincipal User authUser) {

        Order order = orderService.getOrderById(authUser, orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isSeller = order.getItems().stream()
                .anyMatch(oi -> oi.getProduct().getSeller().getId().equals(authUser.getId()));

        if (!isSeller) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Order updated = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/seller/sales")
    public ResponseEntity<List<Map<String, Object>>> getSellerSales(@AuthenticationPrincipal User authUser) {
        List<OrderItem> items = orderService.getOrdersForSeller(authUser);
        List<Map<String, Object>> response = items.stream().map(i -> {
            Map<String, Object> map = new HashMap<>();
            map.put("product", i.getProduct());
            map.put("quantity", i.getQuantity());
            map.put("unitPrice", i.getUnitPrice());
            map.put("buyer", i.getOrder().getBuyer());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

}
