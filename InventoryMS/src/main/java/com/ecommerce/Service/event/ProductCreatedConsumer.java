package com.ecommerce.Service.event;

import com.ecommerce.Service.dto.ProductCreatedEventDTO;
import com.ecommerce.Service.dto.UpdateInventoryRequestDTO;
import com.ecommerce.Service.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductCreatedConsumer {

    @Autowired
    private InventoryService inventoryService;

    @KafkaListener(
            topics = "product-created",
            groupId = "inventory-group"
    )
    public void consume(ProductCreatedEventDTO event) {
        System.out.println(event);
        UpdateInventoryRequestDTO dto = new UpdateInventoryRequestDTO(event.getProductId(), event.getQuantity());
        inventoryService.updateStock(dto);
    }
}