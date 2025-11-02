package com.utkarsh.inventory_service;

import com.utkarsh.inventory_service.model.Inventory;
import com.utkarsh.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	// Add this @Bean
	// This will run when the application starts
	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			// Create a new inventory item that IS in stock
			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("iphone_15");
			inventory1.setQuantity(100);

			// Create a new inventory item that IS NOT in stock
			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("iphone_15_red");
			inventory2.setQuantity(0);

			// Save both to the database
			inventoryRepository.save(inventory1);
			inventoryRepository.save(inventory2);
		};
	}
}