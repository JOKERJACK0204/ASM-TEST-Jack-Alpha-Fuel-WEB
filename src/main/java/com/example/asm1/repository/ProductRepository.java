package com.example.asm1.repository;

import com.example.asm1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // LẤY SẢN PHẨM MỚI NHẤT TRƯỚC
    List<Product> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Product> findByKeyword(String keyword);

    @Query("""
SELECT p FROM Product p
WHERE LOWER(p.name) LIKE '%whey%'
   OR LOWER(p.name) LIKE '%protein%'
""")
    List<Product> findWheyOrProtein();

}
