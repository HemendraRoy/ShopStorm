package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.services.UserService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User saved = userService.register(user);
        saved.setPassword(null);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get user info by ID
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) return ResponseEntity.notFound().build();
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // Delete user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Optional: Update user info
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        User updated = userService.updateUser(userId, userDetails);
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }
}
