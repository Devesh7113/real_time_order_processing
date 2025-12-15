package com.example.real_time_order_processing.modules.productService.controller;

import com.example.real_time_order_processing.modules.productService.dto.ProductDTO;
import com.example.real_time_order_processing.modules.productService.dto.ProductCreateDTO;
import com.example.real_time_order_processing.modules.productService.service.ProductService;
import com.example.real_time_order_processing.utils.ExceptionUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product")
public class ProductController
{
    private final ProductService productService;

    @GetMapping
    ResponseEntity<?> getAllProduct()
    {
        try
        {
            List<ProductDTO> productDTOList = productService.getAllProducts();

            return ResponseEntity.ok(productDTOList);
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @PostMapping("/add")
    ResponseEntity<?> addProduct(@RequestBody @Valid ProductCreateDTO request)
    {
        try
        {
            String response = productService.addNewProduct(request);
            return ResponseEntity.ok(response);
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @PutMapping("/update")
    ResponseEntity<?> updateProduct(@RequestParam Long id, @RequestBody @Valid ProductCreateDTO request)
    {
        try
        {
            String response = productService.updateProduct(id, request);
            return ResponseEntity.ok(response);
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @DeleteMapping("/delete")
    ResponseEntity<?> deleteProduct(@RequestParam Long id)
    {
        try
        {
            String response = productService.deleteProduct(id);
            return ResponseEntity.ok(response);
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }
}
