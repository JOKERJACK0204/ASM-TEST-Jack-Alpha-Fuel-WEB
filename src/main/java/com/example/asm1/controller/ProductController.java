package com.example.asm1.controller;

import com.example.asm1.entity.Product;
import com.example.asm1.repository.ProductRepository;
import com.example.asm1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/products")
public class ProductController {

    // Repository dùng để truy vấn trực tiếp sản phẩm
    @Autowired
    private ProductRepository productRepository;

    // Service xử lý nghiệp vụ (lưu sản phẩm + upload ảnh)
    @Autowired
    private ProductService productService;

    // ===== DANH SÁCH SẢN PHẨM (NGƯỜI DÙNG) =====
    @GetMapping("")
    public String list(Model model) {

        // Lấy toàn bộ sản phẩm từ DB
        model.addAttribute("products", productRepository.findAll());

        // Trả về trang danh sách sản phẩm
        return "product/list";
    }

    // ===== CHI TIẾT SẢN PHẨM =====
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {

        // Lấy sản phẩm theo id và truyền sang view
        model.addAttribute("product",
                productRepository.findById(id).orElse(null));

        return "product/detail";
    }

    // ===== TRANG QUẢN LÝ SẢN PHẨM (ADMIN) =====
    @GetMapping("/crud")
    public String crud(Model model) {

        // Danh sách sản phẩm để hiển thị bảng
        model.addAttribute("products", productRepository.findAll());

        // Đối tượng product rỗng cho form thêm mới
        model.addAttribute("product", new Product());

        return "product/crud";
    }

    // ===== LOAD DỮ LIỆU SẢN PHẨM LÊN FORM ĐỂ SỬA =====
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        // Sản phẩm cần sửa
        model.addAttribute("product",
                productRepository.findById(id).orElse(null));

        // Danh sách sản phẩm để hiển thị bảng
        model.addAttribute("products", productRepository.findAll());

        return "product/crud";
    }

    // ===== THÊM MỚI / CẬP NHẬT SẢN PHẨM =====
    @PostMapping("/create")
    public String create(
            @ModelAttribute Product product,      // Dữ liệu sản phẩm từ form
            @RequestParam MultipartFile imageFile  // File ảnh upload
    ) throws IOException {

        // Gọi service để xử lý lưu sản phẩm + upload ảnh
        productService.save(product, imageFile);

        // Sau khi lưu → quay lại trang quản lý
        return "redirect:/products/crud";
    }

    // ===== XOÁ SẢN PHẨM =====
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        // Xoá sản phẩm theo id
        productRepository.deleteById(id);

        return "redirect:/products/crud";
    }




//    trang product sort
// ===== SORT SẢN PHẨM THEO TỪ KHOÁ =====
@GetMapping("/sort")
public String sort(
        @RequestParam String keyword,
        Model model
) {
    model.addAttribute("products",
            productRepository.findByKeyword(keyword));

    // DÙNG LẠI VIEW product/list
    return "product/list";
}

    @GetMapping("/sort2")
    public String sort(Model model) {
        model.addAttribute("products",
                productRepository.findWheyOrProtein());
        return "product/list";
    }


}
