package com.ecommerce.Service.repository;

import com.ecommerce.Service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderId = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}