package com.example.asm1.service;

import com.example.asm1.entity.Order;
import com.example.asm1.entity.User;


import java.util.List;

public interface OrderService {
    List<Order> findByUser(User user);
    Order findById(Integer id);
}
