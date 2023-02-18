package com.emma_ea.order_service.controller;

import com.emma_ea.order_service.dto.OrderRequest;
import com.emma_ea.order_service.dto.OrderResponse;
import com.emma_ea.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    public CompletableFuture<String> createOrder(@RequestBody OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> orderService.createOrder(orderRequest));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        log.error(runtimeException.getMessage(), runtimeException);
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong placing your order.");
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
