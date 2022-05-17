package com.swa.application.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

	public void addOrderLine(OrderLine ol) {
		if(orderLines == null) {
			orderLines = new ArrayList<>();
		}
		orderLines.add(ol);
	}
}
