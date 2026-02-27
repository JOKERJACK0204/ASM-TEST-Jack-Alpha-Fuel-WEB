package com.example.asm1.repository;

import com.example.asm1.entity.CartItem;
import com.example.asm1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    CartItem findByUserAndProductId(User user, Long productId);

    @Modifying
    @Transactional
    void deleteByUser(User user);
}
