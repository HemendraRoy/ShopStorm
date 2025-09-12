package com.shopstorm.ShopStorm.services;

import com.shopstorm.ShopStorm.entities.CartItem;
import com.shopstorm.ShopStorm.entities.Product;
import com.shopstorm.ShopStorm.entities.User;
import com.shopstorm.ShopStorm.repositories.CartItemRepository;
import com.shopstorm.ShopStorm.repositories.ProductRepository;
import com.shopstorm.ShopStorm.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<CartItem> getCartForUser(User user) {
        return cartRepo.findByUser(user);
    }

    public CartItem addToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }

        Optional<CartItem> existing = cartRepo.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new IllegalArgumentException("Not enough stock to increase quantity for product: " + product.getName());
            }
            item.setQuantity(newQuantity);
            return cartRepo.save(item);
        } else {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
            return cartRepo.save(item);
        }
    }

    public void removeItem(User user, Long cartItemId) {
        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot remove other user's cart item");
        }
        cartRepo.delete(item);
    }

    public void clearCart(User user) {
        List<CartItem> items = cartRepo.findByUser(user);
        cartRepo.deleteAll(items);
    }
}
