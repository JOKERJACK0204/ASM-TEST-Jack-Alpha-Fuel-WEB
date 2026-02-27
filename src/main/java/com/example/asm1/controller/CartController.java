package com.example.asm1.controller;

import com.example.asm1.entity.User;
import com.example.asm1.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ===== XEM GIỎ HÀNG =====
    @GetMapping("")
    public String cart(Model model, HttpSession session) {

        // Lấy user từ session
        User user = (User) session.getAttribute("user");

        // Chưa đăng nhập → login
        if (user == null) return "redirect:/auth/login";

        // Danh sách sản phẩm trong giỏ
        model.addAttribute("items", cartService.getItems(user));

        // Tổng tiền
        model.addAttribute("total", cartService.getTotal(user));

        return "cart/cart";
    }

    // ===== THÊM VÀO GIỎ =====
    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        // Thêm sản phẩm vào giỏ
        cartService.addToCart(user, productId);

        // Cập nhật số lượng giỏ hàng
        session.setAttribute("cartCount", cartService.countDistinct(user));

        return "redirect:/cart";
    }

    // ===== CẬP NHẬT SỐ LƯỢNG =====
    @PostMapping("/update")
    public String update(
            @RequestParam Long itemId,
            @RequestParam int quantity,
            HttpSession session
    ) {
        // Cập nhật giỏ hàng
        cartService.updateQuantity(itemId, quantity);

        // Cập nhật lại cartCount
        User user = (User) session.getAttribute("user");
        if (user != null) {
            session.setAttribute("cartCount", cartService.countDistinct(user));
        }

        return "redirect:/cart";
    }

    // ===== XOÁ 1 SẢN PHẨM =====
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {

        // Xoá item
        cartService.deleteItem(id);

        // Cập nhật cartCount
        User user = (User) session.getAttribute("user");
        if (user != null) {
            session.setAttribute("cartCount", cartService.countDistinct(user));
        }

        return "redirect:/cart";
    }

    // ===== XOÁ TẤT CẢ =====
    @GetMapping("/clear")
    public String clear(HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user != null) {
            cartService.clearCart(user);
            session.setAttribute("cartCount", 0);
        }
        return "redirect:/cart";
    }

    // ===== THANH TOÁN =====
    @GetMapping("/checkout")
    public String checkout(HttpSession session) {
        // Reset số lượng giỏ
        session.setAttribute("cartCount", 0);
        return "checkout/checkout";
    }
}
