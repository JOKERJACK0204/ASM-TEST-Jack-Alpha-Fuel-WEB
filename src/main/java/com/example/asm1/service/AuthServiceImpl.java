package com.example.asm1.service;

import com.example.asm1.entity.User;
import com.example.asm1.repository.CartItemRepository;
import com.example.asm1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // ===== KIỂM TRA ĐĂNG NHẬP =====
    @Override
    public User login(String email, String password) {

        // ===== CHƯA NHẬP EMAIL HOẶC PASSWORD =====
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("EMAIL_PASSWORD_EMPTY");
        }

        // ===== TÌM USER THEO EMAIL =====
        User user = userRepository.findByEmail(email);

        // ===== SAI TÀI KHOẢN =====
        if (user == null) {
            throw new RuntimeException("EMAIL_NOT_FOUND");
        }

        // ===== SAI MẬT KHẨU =====
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("PASSWORD_WRONG");
        }

        // ===== ĐĂNG NHẬP THÀNH CÔNG =====
        return user;
    }

    // ===== ĐĂNG KÝ TÀI KHOẢN =====
    @Override
    public void register(String fullname, String email, String password) {

        // Email đã tồn tại
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("EMAIL_EXIST");
        }

        // Tạo user mới
        User user = new User();
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("USER");

        // Lưu DB
        userRepository.save(user);
    }

    // ===== ĐẾM SỐ SẢN PHẨM TRONG GIỎ =====
    @Override
    public int getCartCount(User user) {
        return cartItemRepository.findByUser(user).size();
    }
}
