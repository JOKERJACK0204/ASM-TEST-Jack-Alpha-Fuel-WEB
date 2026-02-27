package com.example.asm1.service;

import com.example.asm1.entity.Product;
import com.example.asm1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    // Thư mục lưu ảnh upload trên máy
    private static final String UPLOAD_DIR = "D:/java_upload/images/";

    @Autowired
    private ProductRepository productRepository;

    // ===== LƯU SẢN PHẨM (THÊM MỚI / CẬP NHẬT) =====
    @Override
    public void save(Product product, MultipartFile imageFile) throws IOException {

        // ===== TRƯỜNG HỢP CẬP NHẬT =====
        if (product.getId() != null) {

            // Lấy sản phẩm cũ trong DB
            Product old = productRepository.findById(product.getId()).orElse(null);

            // Nếu không upload ảnh mới → giữ ảnh cũ
            if (old != null && (product.getImage() == null || product.getImage().isEmpty())) {
                product.setImage(old.getImage());
            }
        }

        // ===== XỬ LÝ UPLOAD ẢNH =====
        if (!imageFile.isEmpty()) {

            // Tạo thư mục nếu chưa tồn tại
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            // Tạo tên file ngẫu nhiên để tránh trùng
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            // Lưu file vào thư mục
            imageFile.transferTo(new File(UPLOAD_DIR + fileName));

            // Lưu đường dẫn ảnh vào DB
            product.setImage("/images/" + fileName);
        }

        // ===== LƯU SẢN PHẨM VÀO DATABASE =====
        productRepository.save(product);
    }
}
