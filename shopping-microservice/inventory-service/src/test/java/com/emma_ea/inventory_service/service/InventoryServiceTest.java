package com.emma_ea.inventory_service.service;

import com.emma_ea.inventory_service.model.Inventory;
import com.emma_ea.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private static final InventoryRepository repository = Mockito.mock(InventoryRepository.class);
    private static final InventoryService serviceMock =
            Mockito.mock(InventoryService.class, Mockito.withSettings().useConstructor(repository));

    @Test
    public void productInStock() {

        Mockito.when(serviceMock.inStock("111")).thenReturn(true);

        serviceMock.inStock("111");

        Mockito.verify(serviceMock).inStock("111");

    }


}