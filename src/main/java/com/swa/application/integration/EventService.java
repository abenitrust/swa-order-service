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

@Service
public class EventService {
	private KafkaTemplate<String, Order> kafkaOrderTemplate;
	private EmailService emailService;

	@Value("${event.topics.order-placed}")
	private String orderPlacedTopic;

	private static final Logger logger = LoggerFactory.getLogger(EventService.class);

	@Autowired
	public EventService(
			KafkaTemplate<String, Order> kafkaOrderTemplate,
			EmailService emailService) {
		this.kafkaOrderTemplate = kafkaOrderTemplate;
		this.emailService = emailService;
	}

	public void sendOrderPlacedMsg(Order order) {
		logger.info("Publish " + orderPlacedTopic + " topic");
		kafkaOrderTemplate.send(orderPlacedTopic, order);
	}
}