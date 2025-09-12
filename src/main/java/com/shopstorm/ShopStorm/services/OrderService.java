package com.shopstorm.ShopStorm.services;

import com.shopstorm.ShopStorm.entities.*;
import com.shopstorm.ShopStorm.repositories.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository orderRepo, OrderItemRepository orderItemRepo,
                        CartItemRepository cartRepo, ProductRepository productRepo,
                        UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public Order placeOrder(Long userId) {
        User buyer = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<CartItem> cartItems = cartRepo.findByUser(buyer);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderStatus(OrderStatus.PENDING);

        double total = 0.0;
        List<OrderItem> items = new ArrayList<>();
        List<Product> updatedProducts = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product p = productRepo.findById(ci.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (p.getStock() < ci.getQuantity())
                throw new RuntimeException("Not enough stock for product: " + p.getName());

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(p.getPrice());
            items.add(oi);

            total += p.getPrice() * ci.getQuantity();
            p.setStock(p.getStock() - ci.getQuantity());
            updatedProducts.add(p);
        }

        order.setTotalPrice(total);
        order.setItems(items);

        Order saved = orderRepo.save(order);
        productRepo.saveAll(updatedProducts);
        cartRepo.deleteAll(cartItems);

        return saved;
    }

    public List<Order> getOrdersByUser(Long userId) {
        User buyer = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepo.findByBuyer(buyer);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status, Long sellerId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isSeller = order.getItems().stream()
                .anyMatch(i -> i.getProduct().getSeller().getId().equals(sellerId));

        if (!isSeller) return null;

        order.setOrderStatus(status);
        return orderRepo.save(order);
    }
}
