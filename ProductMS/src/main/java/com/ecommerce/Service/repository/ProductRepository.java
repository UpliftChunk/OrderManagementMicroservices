package com.ecommerce.Service.repository;

import com.ecommerce.Service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
