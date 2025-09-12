package com.shopstorm.ShopStorm.controllers;

import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.services.UserService;
import com.shopstorm.ShopStorm.security.JwtUtil;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(email);

        Map<String, Object> response = new HashMap<>();
        user.setPassword(null); // hide password
        response.put("user", user);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
