package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.CartItem;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.services.CartService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) { this.cartService = cartService; }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(cartService.getCartForUser(currentUser));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<CartItem> addToCart(@AuthenticationPrincipal User currentUser,
                                              @PathVariable Long productId,
                                              @RequestParam int quantity) {
        CartItem item = cartService.addToCart(currentUser, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal User currentUser,
                                           @PathVariable Long cartItemId) {
        cartService.removeItem(currentUser, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User currentUser) {
        cartService.clearCart(currentUser);
        return ResponseEntity.noContent().build();
    }
}
