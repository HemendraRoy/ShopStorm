package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.services.UserService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User saved = userService.register(user);
        saved.setPassword(null);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        currentUser.setPassword(null);
        return ResponseEntity.ok(currentUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal User currentUser) {
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
