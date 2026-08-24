package com.himanshu.transactionaleventlistener.util;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.himanshu.transactionaleventlistener.records.OrderCreatedEvent;

@Component
public class OrderNotificationListener {

    // This method will be called immediately when the event is published, regardless of the transaction state.
    @EventListener
    public void onEventImmediate(OrderCreatedEvent event) {
        System.out.println("Immediate Notification: Order created with ID: " + event.orderId() +
                ", Customer Email: " + event.customerEmail() +
                ", Amount: " + event.amount());
    }

    // This method will be called after the transaction is successfully committed.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendEmailNotification(OrderCreatedEvent event) {
        // Simulate sending an email notification
        System.out.println("Sending email notification for Order ID: " + event.orderId() +
                ", Customer Email: " + event.customerEmail() +
                ", Amount: " + event.amount());
    }

    // This method will be called if the transaction is rolled back.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleRollback(OrderCreatedEvent event) {
        // This method will be called if the transaction is rolled back.
        System.out.println("Transaction rolled back for Order ID: " + event.orderId() +
                ", Customer Email: " + event.customerEmail() +
                ", Amount: " + event.amount());
    }
}
