package com.example.real_time_order_processing.modules.productService.repository;

import com.example.real_time_order_processing.modules.productService.dto.ProductDTO;
import com.example.real_time_order_processing.modules.productService.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>
{
    @Query("SELECT new com.example.real_time_order_processing.modules.productService.dto.ProductDTO(p.id, p.name, p.description, p.price, p.stockQuantity, p.category, p.imageUrl) FROM Product p")
    List<ProductDTO> findAllProduct();

    Optional<Product> findBySku(String sku);
}
