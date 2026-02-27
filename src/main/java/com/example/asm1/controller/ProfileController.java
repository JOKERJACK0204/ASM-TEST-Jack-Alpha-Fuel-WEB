package com.example.asm1.controller;

import com.example.asm1.entity.User;
import com.example.asm1.entity.Order;
import com.example.asm1.repository.UserRepository;
import com.example.asm1.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        List<Order> orders = orderService.findByUser(user);

        model.addAttribute("orders", orders);
        model.addAttribute("user", user);

        return "profile/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);

        return "profile/edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String fullname,
                                @RequestParam String email,
                                HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        user.setFullname(fullname);
        user.setEmail(email);

        userRepository.save(user);

        session.setAttribute("user", user);

        return "redirect:/profile";
    }
}