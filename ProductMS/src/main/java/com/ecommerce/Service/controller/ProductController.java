package com.ecommerce.Service.controller;

import com.ecommerce.Service.dto.ProductRequestDTO;
import com.ecommerce.Service.dto.ProductResponseDTO;
import com.ecommerce.Service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // POST /products
    @PostMapping
    public ProductResponseDTO createProduct(
            @RequestBody ProductRequestDTO dto
    ) {
        return productService.createProduct(dto);
    }

    // GET /products/{id}
    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(
            @PathVariable Long id
    ) {
        return productService.getProductById(id);
    }

    // GET /products
    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }
}