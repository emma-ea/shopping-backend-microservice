package com.emma_ea.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryResponse {
    private String skuCode;
    private boolean isInStock;
}
