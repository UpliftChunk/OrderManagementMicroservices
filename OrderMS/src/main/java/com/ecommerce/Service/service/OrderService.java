package com.ecommerce.Service.service;

import com.ecommerce.Service.config.client.FeignInventoryClient;
import com.ecommerce.Service.config.client.RestTemplateInventoryClient;
import com.ecommerce.Service.dto.*;
import com.ecommerce.Service.entity.Order;
import com.ecommerce.Service.entity.OrderItem;
import com.ecommerce.Service.entity.OrderStatus;
import com.ecommerce.Service.exceptionHandler.exception.OrderNotFoundException;
import com.ecommerce.Service.repository.OrderRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplateInventoryClient restTemplateInventoryClient;

    @Autowired
    private FeignInventoryClient feignInventoryClient;

    @Transactional
    public OrderResponseDTO createOrder(OrderCreateRequestDTO request) {


        // 1. Call Inventory Service

        List<InventoryRequestDTO> ListOfInventoryRequestDTO = request.getItems().stream()
                .map(i -> {
                    InventoryRequestDTO dto = new InventoryRequestDTO();
                    dto.setProductId(i.getProductId());
                    dto.setQuantity(i.getQuantity());
                    return dto;
                })
                .toList();

        boolean reserved = restTemplateInventoryClient.reserveStock(ListOfInventoryRequestDTO);


        // 2. Create order (CREATED)
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);

        order.setUserId(request.getUserId());

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (OrderItemDTO dto : request.getItems()) {

            OrderItem item = new OrderItem();
            item.setProductId(dto.getProductId());
            item.setQuantity(dto.getQuantity());
            item.setPrice(dto.getPrice());
            item.setOrder(order);

            total += dto.getPrice() * dto.getQuantity();
            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        orderRepository.save(order);

        return new OrderResponseDTO(true,"Order Created");
    }

    @Transactional
    public OrderResponseDTO cancelOrder(OrderCancelRequestDTO request) {


        // 1. check if order exists and collect order items if they exist.

        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() ->
                        new OrderNotFoundException("Order not found for order ID: " + request.getOrderId()));


        List<InventoryRequestDTO> ListOfInventoryRequestDTO = order.getItems().stream()
                .map(i -> {
                    InventoryRequestDTO dto = new InventoryRequestDTO();
                    dto.setProductId(i.getProductId());
                    dto.setQuantity(i.getQuantity());
                    return dto;
                })
                .toList();

        // 2. Call Inventory Service to release inventory
        try {
            feignInventoryClient.releaseStock(ListOfInventoryRequestDTO);
        } catch (FeignException e) {
            throw e;
        }

        // 3. change order status to cancel

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);


        return new OrderResponseDTO(true,"Order Cancel");
    }

    // GET Order
    public Order getOrder(Long orderId) {

        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found for order ID: " + orderId));
    }
}