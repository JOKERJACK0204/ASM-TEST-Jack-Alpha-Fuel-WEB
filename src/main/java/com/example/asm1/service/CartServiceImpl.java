package com.example.asm1.service;

import com.example.asm1.entity.CartItem;
import com.example.asm1.entity.Product;
import com.example.asm1.entity.User;
import com.example.asm1.repository.CartItemRepository;
import com.example.asm1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    // ===== LẤY DANH SÁCH GIỎ HÀNG =====
    @Override
    public List<CartItem> getItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    // ===== TÍNH TỔNG TIỀN =====
    @Override
    public double getTotal(User user) {
        return cartItemRepository.findByUser(user)
                .stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
    }

    // ===== THÊM SẢN PHẨM VÀO GIỎ =====
    @Override
    public void addToCart(User user, Long productId) {

        // Tìm sản phẩm
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;

        // Kiểm tra sản phẩm đã có trong giỏ chưa
        CartItem item = cartItemRepository.findByUserAndProductId(user, productId);

        if (item == null) {
            // Chưa có → tạo mới
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(1);
        } else {
            // Đã có → tăng số lượng
            item.setQuantity(item.getQuantity() + 1);
        }

        cartItemRepository.save(item);
    }

    // ===== CẬP NHẬT SỐ LƯỢNG =====
    @Override
    public void updateQuantity(Long itemId, int quantity) {

        CartItem item = cartItemRepository.findById(itemId).orElse(null);
        if (item == null) return;

        if (quantity <= 0) {
            // Số lượng <= 0 → xoá
            cartItemRepository.delete(item);
        } else {
            // Cập nhật số lượng
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    // ===== XOÁ 1 ITEM =====
    @Override
    public void deleteItem(Long id) {
        cartItemRepository.deleteById(id);
    }

    // ===== XOÁ TOÀN BỘ GIỎ =====
    @Override
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    // ===== ĐẾM SỐ SP KHÁC NHAU =====
    @Override
    public int countDistinct(User user) {
        return cartItemRepository.findByUser(user).size();
    }
}
