package com.ecommerce.Service.service;

import com.ecommerce.Service.dto.InventoryRequestDTO;
import com.ecommerce.Service.dto.UpdateInventoryRequestDTO;
import com.ecommerce.Service.entity.Inventory;
import com.ecommerce.Service.exceptionHandler.exception.InsufficientStockException;
import com.ecommerce.Service.exceptionHandler.exception.InventoryNotFoundException;
import com.ecommerce.Service.exceptionHandler.exception.NoReservedStockException;
import com.ecommerce.Service.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    // RESERVE STOCK
    @Transactional
    public void reserveStock(List<InventoryRequestDTO> inventoryItems) {

        ArrayList<Long> StockInsufficientProducts = new ArrayList<>();
        ArrayList<Long> NotFoundProducts = new ArrayList<>();

        for(InventoryRequestDTO inventoryItem: inventoryItems){
            Optional<Inventory> inv = inventoryRepository.findById(inventoryItem.getProductId());
            if(inv.isEmpty()) {
                NotFoundProducts.add(inventoryItem.getProductId());
                continue;
            }

            int updated = inventoryRepository.reserveStock(inventoryItem.getProductId(), inventoryItem.getQuantity());

            if (updated == 0) {
                StockInsufficientProducts.add(inventoryItem.getProductId());
            }
        }

        if(!NotFoundProducts.isEmpty()){
            throw new InventoryNotFoundException("Inventory not found for mentioned product ID's", NotFoundProducts);
        }

        if(!StockInsufficientProducts.isEmpty()){
            throw new InsufficientStockException("Insufficient stock for mentioned product ID's", StockInsufficientProducts);
        }
    }

    // RELEASE STOCK
    @Transactional
    public void releaseStock(List<InventoryRequestDTO> inventoryItems) {

        ArrayList<Long> StockInsufficientProducts = new ArrayList<>();
        ArrayList<Long> NotFoundProducts = new ArrayList<>();

        for(InventoryRequestDTO inventoryItem: inventoryItems){

            Optional<Inventory> inv = inventoryRepository.findById(inventoryItem.getProductId());
            if(inv.isEmpty()) {
                NotFoundProducts.add(inventoryItem.getProductId());
                continue;
            }

            int updated = inventoryRepository.releaseStock(inventoryItem.getProductId(), inventoryItem.getQuantity());

            if (updated == 0) {
                StockInsufficientProducts.add(inventoryItem.getProductId());
            }
        }

        if(!NotFoundProducts.isEmpty()){
            throw new InventoryNotFoundException("Inventory not found for mentioned product ID's", NotFoundProducts);
        }
        
        if(!StockInsufficientProducts.isEmpty()){
            throw new NoReservedStockException("No reserved stock to release for mentioned product ID's", StockInsufficientProducts);
        }

    }

    // GET INVENTORY
    public Inventory getInventory(Long productId) {

        return inventoryRepository.findById(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found for product ID: " + productId));
    }


    // UPDATE STOCK QUANTITY
    @Transactional
    public void updateStock(UpdateInventoryRequestDTO inventoryItem) {

            Optional<Inventory> inv = inventoryRepository.findById(inventoryItem.getProductId());
            if(inv.isEmpty()) {
                Inventory new_item = new Inventory();
                new_item.setProductId(inventoryItem.getProductId());
                new_item.setAvailableQuantity(inventoryItem.getQuantity());
                new_item.setReservedQuantity(0);
                inventoryRepository.save(new_item);
            }
            else {
                Inventory item = inv.get();
                item.setAvailableQuantity(item.getAvailableQuantity()+inventoryItem.getQuantity());

                inventoryRepository.save(item);
            }
    }

}