package com.ecommerce.Service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long userId;

        private String name;

        @Column(unique = true, nullable = false)
        private String email;

        private String password;

        @Enumerated(EnumType.STRING)
        private RoleType role;
}
