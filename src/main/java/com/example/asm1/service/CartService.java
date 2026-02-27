package com.example.asm1.service;

import com.example.asm1.entity.CartItem;
import com.example.asm1.entity.User;
import com.example.asm1.service.CartServiceImpl;
import java.util.List;

public interface CartService {

    List<CartItem> getItems(User user);

    double getTotal(User user);

    void addToCart(User user, Long productId);

    void updateQuantity(Long itemId, int quantity);

    void deleteItem(Long id);

    void clearCart(User user);

    int countDistinct(User user);
}
