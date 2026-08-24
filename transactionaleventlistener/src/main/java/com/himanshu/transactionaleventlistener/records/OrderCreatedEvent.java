package com.himanshu.transactionaleventlistener.records;

public record OrderCreatedEvent(Long orderId, String customerEmail, double amount) {
    
}