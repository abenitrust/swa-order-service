package com.swa.application.controller;

import com.swa.application.domain.OrderCustomerDto;
import com.swa.application.domain.ShoppingCart;
import com.swa.application.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.swa.application.domain.Order;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
	@Autowired
	private OrderService orderService;
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


	@GetMapping("/{orderNumber}")
	public ResponseEntity<?> getById(@PathVariable String orderNumber) {
		try {
			return new ResponseEntity<>( orderService.findById(orderNumber), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/add-customer")
	public ResponseEntity<?> addCustomer(@RequestBody OrderCustomerDto orderCustomer) {
		try {
			orderService.addCustomer(orderCustomer);
			return new ResponseEntity<>("Success!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/place/{orderNumber}")
	public ResponseEntity<?> place(@PathVariable String orderNumber) {
		try {
			orderService.place(orderNumber);
			return new ResponseEntity<>("Success!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/prepare")
	public ResponseEntity<?> prepare(@RequestBody ShoppingCart cart) {
		try {
			return new ResponseEntity<>(orderService.prepare(cart), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		try {
			return new ResponseEntity<>( orderService.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{orderNumber}")
	public ResponseEntity<?> delete(@PathVariable String orderNumber) {
		try {
			orderService.delete(orderNumber);
			return new ResponseEntity<>("Success!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody Order order) {
		try {
			orderService.update(order);
			return new ResponseEntity<>("Success!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/customer/{customerID}")
	public ResponseEntity<?> getOrderByCustomerID(@PathVariable String customerID) {
		try {
			return new ResponseEntity<>(orderService.findByCustomerID(customerID), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
