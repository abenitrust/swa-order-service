package com.swa.application.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.swa.application.domain.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Order findByOrderNumber(String orderNumber);
    List<Order> findAll();
    List<Order> findByCustomerID(String customerID);
}
