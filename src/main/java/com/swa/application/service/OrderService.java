package com.swa.application.service;

import com.swa.application.domain.*;
import com.swa.application.exception.DBException;
import com.swa.application.integration.EmailService;
import com.swa.application.integration.EventService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import java.util.List;

import com.swa.application.repository.OrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class OrderService {

	private  OrderRepository orderRepository;
	private  ModelMapper modelMapper;
	private  EventService eventService;
	private CustomerFeignClient customerFeignClient;
	private EmailService emailService;

	private static final Logger logger = LoggerFactory.getLogger(EventService.class);

	@Autowired
	public OrderService(OrderRepository orderRepository,
						ModelMapper modelMapper,
						EventService eventService,
						CustomerFeignClient customerFeignClient,
						EmailService emailService) {
		this.orderRepository = orderRepository;
		this.modelMapper = modelMapper;
		this.eventService = eventService;
		this.emailService = emailService;
		this.customerFeignClient = customerFeignClient;
	}

	public void place(String orderNumber) throws DBException {
		try {
			var order = findById(orderNumber);
			order.setOrderStatus(OrderStatus.PLACED);
			orderRepository.save(order);
			logger.info(order + " placed!");
			eventService.sendOrderPlacedMsg(order);
			emailService.send(order + " placed!");
		} catch (Exception e) {
			throw new DBException(e.getMessage());
		}
	}

	public Order prepare(ShoppingCart cart) throws DBException {
		try {
			Order order = new Order();
			for (CartLine cl : cart.getCartLines()) {
				OrderLine ol = new OrderLine();
				modelMapper.map(cl, ol);
				order.addOrderLine(ol);
			}
			order.setOrderStatus(OrderStatus.CREATED);
			orderRepository.save(order);
			logger.info(order + " prepared.");
			return order;
		} catch (Exception e) {
			throw new DBException(e.getMessage());
		}
	}

	public void addCustomer(OrderCustomerDto orderCustomerDto) throws DBException {
		try {
			var order = findById(orderCustomerDto.getOrderNumber());
			Customer customer = customerFeignClient.getCustomer(orderCustomerDto.getCustomerNumber());
			order.setCustomer(customer);
			order.setCustomerID(customer.getCustomerId());
			orderRepository.save(order);
			logger.info(customer + " added to " + order);
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

	@FeignClient("customer-service")
	@RibbonClient("customer-service")
	interface CustomerFeignClient {
		@GetMapping("/api/v1/customers/{customerId}")
		Customer getCustomer(@PathVariable String customerId);
	}

}
