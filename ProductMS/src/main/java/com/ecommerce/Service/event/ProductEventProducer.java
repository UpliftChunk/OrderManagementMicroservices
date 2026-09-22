package com.ecommerce.Service.event;

import com.ecommerce.Service.dto.ProductCreatedEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductCreated(ProductCreatedEventDTO event) {
        kafkaTemplate.send("product-created",event);
    }
}