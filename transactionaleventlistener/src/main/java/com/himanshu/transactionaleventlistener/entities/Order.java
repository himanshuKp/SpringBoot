package com.himanshu.transactionaleventlistener.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;
    private double amount;

    public Order() {
    }

    public Order(String customerEmail, double amount) {
        this.customerEmail = customerEmail;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public double getAmount() {
        return amount;
    }
}
