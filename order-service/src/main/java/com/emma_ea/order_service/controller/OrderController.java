package com.emma_ea.order_service.controller;

import com.emma_ea.order_service.dto.OrderRequest;
import com.emma_ea.order_service.dto.OrderResponse;
import com.emma_ea.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderRequest orderRequest) {
        orderService.createOrder(orderRequest);
        return "Order created Successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> orders() {
        return orderService.orders();
    }

    @DeleteMapping("order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> delete(@RequestParam(name = "id") String id) {
        return orderService.delete(id);
    }

}
