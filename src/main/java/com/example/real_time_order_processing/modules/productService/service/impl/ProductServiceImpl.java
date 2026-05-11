package com.example.real_time_order_processing.modules.productService.service.impl;

import com.example.real_time_order_processing.modules.productService.dto.ProductDTO;
import com.example.real_time_order_processing.modules.productService.entity.Product;
import com.example.real_time_order_processing.modules.productService.repository.ProductRepository;
import com.example.real_time_order_processing.modules.productService.dto.ProductCreateDTO;
import com.example.real_time_order_processing.modules.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService
{
    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getAllProducts()
    {
        try
        {
            return productRepository.findAllProduct();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Occurred while fetching all product details.");
        }
    }

    @Override
    public String addNewProduct(ProductCreateDTO request)
    {
        String sku = generateSku(request);
        Optional<Product> productOptional = productRepository.findBySku(sku);

        if (productOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Product already present with this name and same category.");
        }

        try
        {
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStockQuantity(request.getStockQuantity());
            product.setCategory(request.getCategory());
            product.setImageUrl(request.getImageUrl());
            product.setSku(sku);

            productRepository.save(product);

            return "Product added successfully.";
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Occurred while adding the product.");
        }
    }

    @Override
    public String deleteProduct(Long id)
    {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isEmpty())
        {
            throw new ResourceNotFoundException("Product is not present.");
        }

        try
        {
            productRepository.deleteById(id);

            return "Product deleted successfully.";
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Occurred while deleting the product.");
        }
    }

    @Override
    public String updateProduct(Long id, ProductCreateDTO request)
    {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isEmpty())
        {
            throw new ResourceNotFoundException("Product is not present.");
        }

        try
        {
            String sku = generateSku(request);

            Product product = optionalProduct.get();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStockQuantity(request.getStockQuantity());
            product.setCategory(request.getCategory());
            product.setImageUrl(request.getImageUrl());
            product.setSku(sku);

            productRepository.save(product);

            return "Product updated successfully";
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Occurred while updating the product.");
        }
    }


    private String generateSku(ProductCreateDTO request)
    {
        return request.getName().toLowerCase() + "_" + request.getCategory().toLowerCase();
    }
}
