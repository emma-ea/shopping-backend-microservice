package com.emma_ea.inventory_service.service;

import com.emma_ea.inventory_service.dto.InventoryResponse;
import com.emma_ea.inventory_service.model.Inventory;
import com.emma_ea.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows   // dev purposes
    public List<InventoryResponse> inStock(List<String> skuCode) {
        log.info("checking inventory");

        log.info("Wait started");
        Thread.sleep(10000);    // simulating slow network connection
        log.info("Wait ended");

        List<Inventory> inventory = inventoryRepository.findBySkuCodeIn(skuCode);
        List<InventoryResponse> responses = inventory.stream()
                .map(this::inventoryDtoMapper)
                .collect(Collectors.toUnmodifiableList());
        log.info("inventory size {}", responses.size());
        return responses;
    }

    private InventoryResponse inventoryDtoMapper(Inventory inventory) {
        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setInStock(inventory.getQuantity() > 0);
        inventoryResponse.setSkuCode(inventory.getSkuCode());
        return inventoryResponse;
    }

}
