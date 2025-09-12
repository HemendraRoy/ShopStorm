package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.CartItem;
import com.shopstorm.ShopStorm.services.CartService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartForUser(userId));
    }

    @PostMapping("/add/{userId}/{productId}")
    public ResponseEntity<CartItem> addToCart(@PathVariable Long userId,
                                              @PathVariable Long productId,
                                              @RequestParam int quantity) {
        CartItem item = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{userId}/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long userId,
                                           @PathVariable Long cartItemId) {
        cartService.removeItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
