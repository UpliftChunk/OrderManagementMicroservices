package com.ecommerce.Service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

        @Id
        private Long productId;

        private Integer availableQuantity;

        private Integer reservedQuantity;
}