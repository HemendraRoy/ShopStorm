package com.shopstorm.ShopStorm.services;

import com.shopstorm.ShopStorm.entities.CartItem;
import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.repositories.CartItemRepository;
import com.shopstorm.ShopStorm.repositories.ProductRepository;
import com.shopstorm.ShopStorm.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public CartService(CartItemRepository cartRepo, UserRepository userRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public List<CartItem> getCartForUser(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepo.findByUser(user);
    }

    public CartItem addToCart(Long userId, Long productId, int quantity) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(quantity);

        return cartRepo.save(item);
    }

    public void removeItem(Long userId, Long cartItemId) {
        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (!item.getUser().getId().equals(userId))
            throw new RuntimeException("Cannot delete this cart item");
        cartRepo.delete(item);
    }

    public void clearCart(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<CartItem> items = cartRepo.findByUser(user);
        cartRepo.deleteAll(items);
    }
}
