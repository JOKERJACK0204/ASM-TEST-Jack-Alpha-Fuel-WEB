package com.example.asm1.controller;

import com.example.asm1.entity.Order;
import com.example.asm1.entity.User;

import com.example.asm1.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Integer id,
                              HttpSession session,
                              Model model) {

        User user = (User) session.getAttribute("user");

        // Chưa login → về login
        if (user == null) {
            return "redirect:/auth/login";
        }

        Order order = orderService.findById(id);

        // Không tồn tại hoặc không phải chủ đơn
        if (order == null ||
                !order.getUser().getId().equals(user.getId())) {
            return "redirect:/profile";
        }

        model.addAttribute("order", order);

        return "order/detail";
    }
}