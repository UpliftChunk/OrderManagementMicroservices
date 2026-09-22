package com.ecommerce.Service.config.client;

import com.ecommerce.Service.dto.InventoryRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "inventory-service",
        url = "http://localhost:8083"
)
public interface FeignInventoryClient {

    @PostMapping("/inventory/release")
    Map<String, Object> releaseStock(
            @RequestBody List<InventoryRequestDTO> request
    );

}