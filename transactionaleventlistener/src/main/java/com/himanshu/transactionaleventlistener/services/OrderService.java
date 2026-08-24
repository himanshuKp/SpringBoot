package com.himanshu.transactionaleventlistener.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.himanshu.transactionaleventlistener.entities.Order;
import com.himanshu.transactionaleventlistener.records.OrderCreatedEvent;
import com.himanshu.transactionaleventlistener.repositories.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order placeOrder(String customerEmail, double amount, boolean forceRollback) {
        System.out.println("Placing order for customer: " + customerEmail + ", amount: " + amount);
        Order order = orderRepository.save(new Order(customerEmail, amount));

        System.out.println("Order placed with ID: " + order.getId());
        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId(), customerEmail, amount));

        if (forceRollback) {
            System.out.println("Forcing rollback for order ID: " + order.getId());
            throw new RuntimeException("Forcing rollback for demonstration purposes.");
        }

        return order;
    }
}
