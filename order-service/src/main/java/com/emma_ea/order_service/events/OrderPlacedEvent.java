package com.emma_ea.order_service.events;

public class OrderPlacedEvent {
    private String orderNumber;

    public OrderPlacedEvent() {}

    public OrderPlacedEvent(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
