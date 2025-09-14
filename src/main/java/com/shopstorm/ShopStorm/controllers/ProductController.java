package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.Order;
import com.shopstorm.ShopStorm.entities.OrderItem;
import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.services.OrderService;
import com.shopstorm.ShopStorm.services.ProductService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final OrderService orderService;
    public ProductController(ProductService productService, OrderService orderService) { this.productService = productService;
        this.orderService = orderService;
    }

    @PostMapping("/{sellerId}")
    public ResponseEntity<Product> addProduct(@PathVariable Long sellerId, @RequestBody Product product) {
        Product saved = productService.addProduct(product, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<Map<String, Object>> response = products.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("product", p);
            map.put("averageRating", productService.getAverageRating(p.getId()));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product == null) return ResponseEntity.notFound().build();

        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("averageRating", productService.getAverageRating(productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Product>> getSellerProducts(@PathVariable Long sellerId) {
        List<Product> products = productService.getProductsBySellerId(sellerId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/seller/product-analytics/{productId}")
    public ResponseEntity<Map<String, Object>> getProductAnalytics(
            @PathVariable Long productId,
            @RequestParam Long sellerId) {

        Product product = productService.getProductById(productId);
        if (product == null) return ResponseEntity.notFound().build();
        if (!product.getSeller().getId().equals(sellerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int totalUnitsSold = 0;
        double totalRevenue = 0.0;

        List<Order> allOrders = orderService.getAllOrders(); // returns a List<Order>
        for (Order order : allOrders) {
            List<OrderItem> items = order.getItems(); // this is a List<OrderItem>
            for (OrderItem item : items) {
                if (item.getProduct().getId().equals(productId)) {
                    totalUnitsSold += item.getQuantity();
                    totalRevenue += item.getQuantity() * item.getUnitPrice();
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalUnitsSold", totalUnitsSold);
        response.put("totalRevenue", totalRevenue);

        return ResponseEntity.ok(response);
    }
    // Add this inside ProductController
    @DeleteMapping("/{productId}/{sellerId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId,
                                              @PathVariable Long sellerId) {
        boolean deleted = productService.deleteProduct(productId, sellerId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    @PutMapping("/{productId}/{sellerId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                                 @PathVariable Long sellerId,
                                                 @RequestBody Product updatedProduct) {
        Product saved = productService.updateProduct(productId, updatedProduct, sellerId);
        if (saved == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(saved);
    }
}
