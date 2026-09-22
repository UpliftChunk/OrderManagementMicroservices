package com.ecommerce.Service.controller;

import com.ecommerce.Service.dto.InventoryRequestDTO;
import com.ecommerce.Service.dto.InventoryResponseDTO;
import com.ecommerce.Service.dto.UpdateInventoryRequestDTO;
import com.ecommerce.Service.dto.UpdateInventoryResponseDTO;
import com.ecommerce.Service.entity.Inventory;
import com.ecommerce.Service.service.InventoryService;
import jakarta.validation.Valid;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Validated
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // RESERVE
    @PostMapping("/reserve")
    public InventoryResponseDTO reserve(@RequestBody List<@Valid InventoryRequestDTO> dto) {
        inventoryService.reserveStock(dto);
        return new InventoryResponseDTO(true,"Stock reserved successfully");
    }

    // RELEASE
    @PostMapping("/release")
    public InventoryResponseDTO release(@RequestBody List<@Valid InventoryRequestDTO> dto) {
        inventoryService.releaseStock(dto);
        return new InventoryResponseDTO(true,"Stock released successfully");
    }

    // GET INVENTORY
    @GetMapping("/{productId}")
    public Inventory getInventory(@PathVariable Long productId) {
        return inventoryService.getInventory(productId);
    }

    // UPDATE STOCK QUANTITY
    @PostMapping("/updateStock")
    public UpdateInventoryResponseDTO updateStock(@RequestBody @Valid UpdateInventoryRequestDTO dto) {
        inventoryService.updateStock(dto);
        return new UpdateInventoryResponseDTO(true,"Quantity updated successfully");
    }
}