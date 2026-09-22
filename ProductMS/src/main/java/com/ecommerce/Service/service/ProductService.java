package com.ecommerce.Service.service;

import com.ecommerce.Service.dto.ProductCreatedEventDTO;
import com.ecommerce.Service.dto.ProductRequestDTO;
import com.ecommerce.Service.dto.ProductResponseDTO;
import com.ecommerce.Service.entity.Product;
import com.ecommerce.Service.event.ProductEventProducer;
import com.ecommerce.Service.exceptionHandler.exception.ResourceNotFoundException;
import com.ecommerce.Service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductEventProducer productEventProducer;

    // CREATE PRODUCT
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());

        Product saved = productRepository.save(product);

        ProductCreatedEventDTO event =
                new ProductCreatedEventDTO(
                        saved.getProductId(),
                        dto.getQuantity()
                );

        productEventProducer.publishProductCreated(event);
        return mapToResponse(saved);
    }

    // GET PRODUCT BY ID
    public ProductResponseDTO getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return mapToResponse(product);
    }

    // GET ALL PRODUCTS
    public List<ProductResponseDTO> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // MAPPER
    private ProductResponseDTO mapToResponse(Product product) {

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());

        return dto;
    }
}