package com.utkarsh.inventory_service.controller;

import com.utkarsh.inventory_service.dto.InventoryResponse;
import com.utkarsh.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestBody List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}