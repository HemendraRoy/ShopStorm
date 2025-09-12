package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.services.ProductService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

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

    @PutMapping("/{productId}/{sellerId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                                 @PathVariable Long sellerId,
                                                 @RequestBody Product updatedProduct) {
        Product saved = productService.updateProduct(productId, updatedProduct, sellerId);
        if (saved == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(saved);
    }
}
