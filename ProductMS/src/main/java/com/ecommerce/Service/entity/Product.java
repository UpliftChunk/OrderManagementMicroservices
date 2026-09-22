package com.ecommerce.Service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long productId;

        private String name;

        private Double price;

        private String description;
}