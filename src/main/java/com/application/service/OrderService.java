package com.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.application.domain.Order;
import com.application.repository.OrderRepository;

@Service
public class OrderService {
	@Autowired
	OrderRepository orderRepository;

	public void add(Order order) {
		orderRepository.save(order);
	}

	public void update(Order order) {
		orderRepository.save(order);
	}

	public Order findByOrderNumber(String orderNumber) {
		return orderRepository.findByOrderNumber(orderNumber);
	}

	public List<Order> findByCustomerID(String customerID) {
		return orderRepository.findByCustomerID(customerID);
	}

	public void delete(String productNumber) {
		Order contact = orderRepository.findByOrderNumber(productNumber);
		orderRepository.delete(contact);
	}

	public List<Order> findAll() {
		return orderRepository.findAll();
	}
}
