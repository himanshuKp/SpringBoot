package com.himanshu.transactionaleventlistener.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.himanshu.transactionaleventlistener.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
