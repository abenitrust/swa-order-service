package com.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.application.domain.Order;
import com.application.domain.Orders;
import com.application.exception.CustomErrorType;
import com.application.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
	@Autowired
	private OrderService orderService;

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@GetMapping("/{orderNumber}")
	public ResponseEntity<?> getOrder(@PathVariable String orderNumber) {
		log.info("GET request for /orders/" + orderNumber);
		Order order = orderService.findByOrderNumber(orderNumber);
		if (order == null) {
			return new ResponseEntity<CustomErrorType>(
					new CustomErrorType("Order with number = " + orderNumber + " is not available"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Order>(order, HttpStatus.OK);
	}

	@GetMapping("/customers/{customerID}")
	public ResponseEntity<?> getOrderByCustomerID(@PathVariable String customerID) {
		log.info("GET request for /orders/customers/" + customerID);
		List<Order> orders = orderService.findByCustomerID(customerID);

		if (orders == null) {
			return new ResponseEntity<CustomErrorType>(
					new CustomErrorType("Order with customerID = " + customerID + " is not available"),
					HttpStatus.NOT_FOUND);
		}

		Orders response = new Orders();
		response.setOrders(orders);
		return new ResponseEntity<Orders>(response, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> addOrder(@RequestBody Order order) {
		log.info("POST request for /orders with body: " + order);
		orderService.add(order);
		return new ResponseEntity<Order>(order, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<?> getAllOrders() {
		log.info("GET request for /orders");
		Orders orders = new Orders(orderService.findAll());
		return new ResponseEntity<Orders>(orders, HttpStatus.OK);
	}
}
