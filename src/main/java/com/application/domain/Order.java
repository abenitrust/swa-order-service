package com.application.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	@Id
	private String orderNumber;
	private String customerID;
	private Customer customer;
	private List<OrderLine> orderLines;
}
