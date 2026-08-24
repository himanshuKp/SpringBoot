package com.himanshu.transactionaleventlistener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.himanshu.transactionaleventlistener.entities.Order;
import com.himanshu.transactionaleventlistener.services.OrderService;


@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/success")
    public ResponseEntity<String> creatSuccessfulOrder() {
        Order order = orderService.placeOrder("alice@example.com", 120.50, false);
        return ResponseEntity.ok("Order #" + order.getId() + " placed successfully.");
    }

    @PostMapping("/failure")
    public ResponseEntity<String> createFailedOrder() {
        try {
            orderService.placeOrder("bob@example.com", 99.99, true);
            return ResponseEntity.ok("Failed order placed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to place order.");
        }
    }
}
