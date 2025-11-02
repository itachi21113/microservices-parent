package com.utkarsh.inventory_service.service;

import com.utkarsh.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true) // readOnly because we are not changing data
    public boolean isInStock(String skuCode) {
        log.info("Checking stock for skuCode: {}", skuCode);

        // Find the inventory item by its skuCode
        // If it's present, check if quantity > 0
        return inventoryRepository.findBySkuCode(skuCode)
                .map(inventory -> inventory.getQuantity() > 0)
                .orElse(false); // If not present, it's not in stock
    }
}