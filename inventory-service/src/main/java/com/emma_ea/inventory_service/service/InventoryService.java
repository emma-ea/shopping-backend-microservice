package com.emma_ea.inventory_service.service;

import com.emma_ea.inventory_service.model.Inventory;
import com.emma_ea.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public boolean inStock(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode).orElseThrow();
        return inventory.getQuantity() != 0;
    }

}
