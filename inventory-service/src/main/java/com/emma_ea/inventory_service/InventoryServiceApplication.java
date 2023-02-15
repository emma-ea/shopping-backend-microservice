package com.emma_ea.inventory_service;

import com.emma_ea.inventory_service.model.Inventory;
import com.emma_ea.inventory_service.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Autowired
	private InventoryRepository inventoryRepository;

	@Override
	public void run(String... args) throws Exception {
		Inventory inventory = new Inventory();
		inventory.setId(1L);
		inventory.setSkuCode("Samsung_s5");
		inventory.setQuantity(200);

		Inventory inventory1 = new Inventory();
		inventory1.setId(2L);
		inventory1.setSkuCode("Samsung_s7_red");
		inventory1.setQuantity(120);

		inventoryRepository.save(inventory);
		inventoryRepository.save(inventory1);
	}
}
