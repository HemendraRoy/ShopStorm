package com.shopstorm.ShopStorm.services;

import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required");
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already in use");
        if (user.getPassword() == null || user.getPassword().isBlank())
            throw new IllegalArgumentException("Password is required");

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }
}
