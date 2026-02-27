package com.example.asm1.service;

import com.example.asm1.entity.User;

public interface AuthService {

    User login(String email, String password);

    void register(String fullname, String email, String password);

    int getCartCount(User user);
}
