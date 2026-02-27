package com.example.asm1.service;

import com.example.asm1.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {

    void save(Product product, MultipartFile imageFile) throws IOException;
}
