package com.example.real_time_order_processing.modules.productService.service;

import com.example.real_time_order_processing.modules.productService.dto.ProductDTO;
import com.example.real_time_order_processing.modules.productService.dto.ProductCreateDTO;

import java.util.List;

public interface ProductService
{
    List<ProductDTO> getAllProducts();

    String addNewProduct(ProductCreateDTO request);

    String deleteProduct(Long id);

    String updateProduct(Long id, ProductCreateDTO request);
}
