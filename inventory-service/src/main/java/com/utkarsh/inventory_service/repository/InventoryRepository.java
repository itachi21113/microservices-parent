package com.utkarsh.inventory_service.repository;

import com.utkarsh.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Spring Data JPA will automatically create the query for us
    // based on this method name
    Optional<Inventory> findBySkuCode(String skuCode);
}