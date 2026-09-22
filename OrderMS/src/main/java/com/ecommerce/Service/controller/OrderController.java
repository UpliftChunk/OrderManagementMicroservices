package com.ecommerce.Service.controller;

import com.ecommerce.Service.dto.InventoryRequestDTO;
import com.ecommerce.Service.dto.OrderCancelRequestDTO;
import com.ecommerce.Service.dto.OrderCreateRequestDTO;
import com.ecommerce.Service.dto.OrderResponseDTO;
import com.ecommerce.Service.entity.Order;
import com.ecommerce.Service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    // CREATE
    @PostMapping("/create")
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderCreateRequestDTO request) {
        return orderService.createOrder(request);
    }

    // CANCEL
    @PostMapping("/cancel")
    public OrderResponseDTO cancelOrder(@Valid @RequestBody OrderCancelRequestDTO request) {
        return orderService.cancelOrder(request);
    }

    // GET ORDER
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }
}
