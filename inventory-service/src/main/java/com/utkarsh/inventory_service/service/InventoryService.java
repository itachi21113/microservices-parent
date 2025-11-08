package com.utkarsh.inventory_service.service;

import com.utkarsh.inventory_service.dto.InventoryResponse;
import com.utkarsh.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @SneakyThrows
    @Transactional(readOnly = true) // readOnly because we are not changing data
    public List<InventoryResponse> isInStock(List<String> skuCode)  {
        log.info("Checking stock for skuCodes: {}", skuCode);

        log.info("Wait Started");
        Thread.sleep(2000); // 10 second delay
        log.info("Wait Ended");

        // 2. Call the new repository method
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                // 3. Map the result to our new DTO
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0) //]
                                .build()
                ).toList();
    }
}
