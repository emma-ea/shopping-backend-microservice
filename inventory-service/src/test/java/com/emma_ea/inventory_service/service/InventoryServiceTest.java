package com.emma_ea.inventory_service.service;

import com.emma_ea.inventory_service.model.Inventory;
import com.emma_ea.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private static final InventoryRepository repository = Mockito.mock(InventoryRepository.class);
    private static final InventoryService serviceMock =
            Mockito.mock(InventoryService.class, Mockito.withSettings().useConstructor(repository));

    @Test
    public void productInStock() {

        List<String> codes = new ArrayList<>();
        codes.add("Samsung_s5");

        Mockito.when(serviceMock.inStock(codes));

        serviceMock.inStock(codes);

        Mockito.verify(serviceMock).inStock(codes);

    }


}