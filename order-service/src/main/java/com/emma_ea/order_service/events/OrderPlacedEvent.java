package com.emma_ea.order_service.events;

public class OrderPlacedEvent {
    private final String orderNumber;

    public OrderPlacedEvent(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    String getOrderNumber() {
        return this.orderNumber;
    }
}
