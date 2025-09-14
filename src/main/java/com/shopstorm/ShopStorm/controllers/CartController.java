package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.CartItem;
import com.shopstorm.ShopStorm.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get all items in a user's cart
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartForUser(userId));
    }

    // Add a product to the cart
    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<CartItem> addToCart(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        CartItem item = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(item);
    }

    // Update quantity of a cart item
    @PutMapping("/{userId}/update/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @RequestParam int quantity
    ) {
        CartItem updatedItem = cartService.updateQuantity(userId, cartItemId, quantity);
        return ResponseEntity.ok(updatedItem);
    }

    // Remove a product from the cart
    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId
    ) {
        cartService.removeItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    // Clear all items from the cart
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
