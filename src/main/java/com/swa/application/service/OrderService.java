package com.swa.application.service;

import com.swa.application.exception.DBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.swa.application.domain.Order;
import com.swa.application.repository.OrderRepository;

@Service
public class OrderService {
	@Autowired
	OrderRepository orderRepository;

	public void add(Order order) throws DBException {
		try {
			orderRepository.save(order);
		} catch (Exception e) {
			throw new DBException(e.getMessage());
		}
	}

	public void update(Order order) throws DBException {
		try {
			orderRepository.save(order);
		} catch (Exception e) {
			throw new DBException(e.getMessage());
		}
	}

	public Order findById(String orderNumber) throws DBException {
		return orderRepository.findById(orderNumber)
				.orElseThrow(() -> new DBException("Order with order number " + orderNumber + " not found!"));
	}

	public List<Order> findAll()  throws DBException{
		try {
			return orderRepository.findAll();
		} catch (Exception e) {
			throw new DBException(e.getMessage());
		}
	}

	public void delete(String orderNumber) throws DBException{
		try {
			var order = orderRepository.findById(orderNumber).orElseThrow(
					() -> new DBException("Order by number " + orderNumber + " not found!")
			);
			orderRepository.delete(order);
		} catch (Exception e) {
			throw new DBException(e.getMessage());
		}
	}

	public List<Order> findByCustomerID(String customerID) throws DBException {
		try {
			return orderRepository.findByCustomerID(customerID);
		} catch (Exception e) {
			throw new DBException(e.getMessage());
		}
	}

}
