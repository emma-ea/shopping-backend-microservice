package com.emma_ea.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponse {
    private String orderNumber;
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
