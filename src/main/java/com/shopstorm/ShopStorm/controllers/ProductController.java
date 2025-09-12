package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.services.ProductService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product,
                                              @AuthenticationPrincipal User currentUser) {
        product.setSeller(currentUser);
        Product saved = productService.addProduct(product);
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

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) return ResponseEntity.notFound().build();

        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("averageRating", productService.getAverageRating(id));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                                 @RequestBody Product updatedProduct,
                                                 @AuthenticationPrincipal User currentUser) {
        Product existing = productService.getProductById(productId);
        if (existing == null) return ResponseEntity.notFound().build();
        if (!existing.getSeller().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Product saved = productService.updateProduct(productId, updatedProduct);
        return ResponseEntity.ok(saved);
    }
}
