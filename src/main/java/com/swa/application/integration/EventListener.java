package com.swa.application.integration;

import com.swa.application.domain.*;
import com.swa.application.service.OrderService;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class EventListener {
	private OrderService orderService;
	private  CustomerFeignClient customerFeignClient;
	private ModelMapper modelMapper;
	private KafkaTemplate<String, Order> kafkaOrderTemplate;
	private EmailService emailService;

	@Value("${event.topics.order-placed}")
	private String orderPlacedTopic;

	private static final Logger log = LoggerFactory.getLogger(EventListener.class);

	@Autowired
	public EventListener(
			OrderService orderService,
		 	CustomerFeignClient customerFeignClient,
			ModelMapper modelMapper,
			KafkaTemplate<String, Order> kafkaOrderTemplate,
			EmailService emailService) {

		this.orderService = orderService;
		this.customerFeignClient = customerFeignClient;
		this.modelMapper = modelMapper;
		this.kafkaOrderTemplate = kafkaOrderTemplate;
		this.emailService = emailService;
	}

	@KafkaListener(topics = "${event.topics.checked-out}")
	public void receiveOrder(ShoppingCart cart) {
		Customer customer = customerFeignClient.getCustomer(cart.getCustomerId());
		Order order = new Order();
		order.setCustomerID(cart.getCustomerId());
		order.setCustomer(customer);

		for(CartLine cl: cart.getCartLines()) {
			OrderLine ol = new OrderLine();
			modelMapper.map(cl, ol);
			order.addOrderLine(ol);
		}

		orderService.add(order);
		log.info("Successfully saved order: " + order);

		sendOrderPlacedMsg(order);
		emailService.send("Order successfully placed");
	}

	public void sendOrderPlacedMsg(Order order) {
		kafkaOrderTemplate.send(orderPlacedTopic, order);
	}

	@FeignClient("customer-service")
	interface CustomerFeignClient {
		@RequestMapping("/api/v1/customer/{customerId}")
		Customer getCustomer(@PathVariable String customerId);
	}
	
	@FeignClient("product-service")
	interface ProductFeignClient {
		@RequestMapping("products/{productId}")
		Customer getProduct(@PathVariable String productId);
	}
}