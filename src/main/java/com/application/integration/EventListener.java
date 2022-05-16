package com.application.integration;

import com.application.domain.Customer;
import com.application.domain.Order;
import com.application.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RefreshScope
@Service
public class EventListener {
	@Autowired
	private OrderService orderService;

	@Autowired
	private CustomerFeignClient customerFeignClient;

	private static final Logger log = LoggerFactory.getLogger(EventListener.class);

	@KafkaListener(topics = {"checkout"})
	public void receiveOrder(ShoppingCartDto cart) {
		Customer customer = null;

		try {
			customer = customerFeignClient.getCustomer(cart.getCustomerId());
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		Order order = new Order();
		order.setCustomerID(cart.getCustomerId());
		order.customer(customer);
		if(cart.getCartLines() != null) {
			order.setOrderLines();
		}			
		orderService.add(order);

		log.info("Successfully saved order: " + order);


	}

	@FeignClient("customer-service")
	interface CustomerFeignClient {
		@RequestMapping("customer/{customerId}")
		public Customer getCustomer(@PathVariable String customerId);
	}
	
	@FeignClient("product-service")
	interface ProductFeignClient {
		@RequestMapping("products/{productId}")
		public Customer getProduct(@PathVariable String productId);
	}
}