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
import org.springframework.web.bind.annotation.GetMapping;
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
		Order order = new Order();
		try {
			Customer customer = customerFeignClient.getCustomer(cart.getCustomerID());
			order.setCustomerID(customer.getCustomerId());
			order.setCustomer(customer);

			for (CartLine cl : cart.getCartLines()) {
				OrderLine ol = new OrderLine();
				modelMapper.map(cl, ol);
				order.addOrderLine(ol);
			}

			orderService.add(order);
			log.info("Successfully saved order: " + order);


		} catch (Exception e) {
			log.error(
					"Error occurred while placing an order " + order +
							"\n" + e.getMessage()
			);
			// TODO:  better to revert the changes in Cart Service using saga pattern
			return;
		}

		sendOrderPlacedMsg(order);
		emailService.send("Order successfully placed");
	}

	public void sendOrderPlacedMsg(Order order) {
		kafkaOrderTemplate.send(orderPlacedTopic, order);
	}

	@FeignClient("customer-service")
	interface CustomerFeignClient {
		@GetMapping("/api/v1/customers/{customerId}")
		Customer getCustomer(@PathVariable String customerId);
	}
	
	@FeignClient("product-service")
	interface ProductFeignClient {
		@GetMapping("/api/v1/products/{productId}")
		Product getProduct(@PathVariable String productId);
	}
}