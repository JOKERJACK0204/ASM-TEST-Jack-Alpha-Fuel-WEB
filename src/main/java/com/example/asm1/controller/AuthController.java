package com.example.asm1.controller;

import com.example.asm1.entity.User;
import com.example.asm1.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ===== HIỂN THỊ FORM LOGIN =====
    @GetMapping("/login")
    public String loginForm() {
        // Trả về trang đăng nhập
        return "auth/login";
    }

    // ===== XỬ LÝ LOGIN =====
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        try {
            // Gọi service kiểm tra đăng nhập
            User user = authService.login(email, password);

            // Lưu user vào session
            session.setAttribute("user", user);

            // Lưu số lượng sản phẩm trong giỏ
            session.setAttribute("cartCount", authService.getCartCount(user));

            // Admin → trang quản lý
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/products/crud";
            }

            // User thường → trang chủ
            return "redirect:/";

        } catch (RuntimeException e) {

            // ===== XỬ LÝ CÁC TRƯỜNG HỢP LỖI =====
            switch (e.getMessage()) {

                case "EMAIL_PASSWORD_EMPTY":
                    model.addAttribute("error", "Vui lòng nhập tài khoản và mật khẩu");
                    break;

                case "EMAIL_NOT_FOUND":
                    model.addAttribute("error", "Sai tài khoản");
                    break;

                case "PASSWORD_WRONG":
                    model.addAttribute("error", "Sai mật khẩu");
                    break;
            }

            return "auth/login";
        }
    }

    // ===== HIỂN THỊ FORM REGISTER =====
    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    // ===== XỬ LÝ REGISTER =====
    @PostMapping("/register")
    public String register(
            @RequestParam String fullname, // Họ tên
            @RequestParam String email,    // Email
            @RequestParam String password, // Mật khẩu
            Model model
    ) {
        try {
            // Gọi service đăng ký
            authService.register(fullname, email, password);

            // Thành công → về trang login
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            // Email đã tồn tại
            model.addAttribute("error", "Email đã tồn tại");
            return "auth/register";
        }
    }

    // ===== LOGOUT =====
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Xoá toàn bộ session
        session.invalidate();
        return "redirect:/";
    }
}
