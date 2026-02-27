package com.example.asm1.controller;

import com.example.asm1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute(
                "products",
                productRepository.findAllByOrderByCreatedAtDesc()
                        .stream()
                        .limit(4)
                        .toList()
        );
        return "home/index";
    }

}
