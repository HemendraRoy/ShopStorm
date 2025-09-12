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

    public OrderService(OrderRepository orderRepo,
                        OrderItemRepository orderItemRepo,
                        CartItemRepository cartRepo,
                        ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    public Order placeOrder(User buyer) {
        List<CartItem> cartItems = cartRepo.findByUser(buyer);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderStatus(OrderStatus.PENDING);

        double total = 0.0;
        List<OrderItem> items = new ArrayList<>();
        List<Product> toSaveProducts = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product p = productRepo.findById(ci.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (p.getStock() < ci.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + p.getName());
            }

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(p.getPrice());
            items.add(oi);

            total += p.getPrice() * ci.getQuantity();

            p.setStock(p.getStock() - ci.getQuantity());
            toSaveProducts.add(p);
        }

        order.setTotalPrice(total);
        order.setItems(items);
        Order savedOrder = orderRepo.save(order);
        productRepo.saveAll(toSaveProducts);
        cartRepo.deleteAll(cartItems);

        return savedOrder;
    }

    public List<Order> getOrdersByUser(User buyer) {
        return orderRepo.findByBuyer(buyer);
    }

    public Order getOrderById(User buyer, Long orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order != null && !order.getBuyer().getId().equals(buyer.getId())) {
            return null;
        }
        return order;
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(status);
        return orderRepo.save(order);
    }

    public List<OrderItem> getOrdersForSeller(User seller) {
        List<Order> allOrders = orderRepo.findAll();
        List<OrderItem> sellerItems = new ArrayList<>();
        for (Order order : allOrders) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct().getSeller().getId().equals(seller.getId())) {
                    sellerItems.add(item);
                }
            }
        }
        return sellerItems;
    }
}
