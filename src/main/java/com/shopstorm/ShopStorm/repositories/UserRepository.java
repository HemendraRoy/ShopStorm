package com.shopstorm.ShopStorm.repositories;

import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRole(String email, Role role);
    boolean existsByEmail(String email);
}