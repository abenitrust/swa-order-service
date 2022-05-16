package com.application.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShoppingCartDto {
    private String shoppingCartNumber;
    private String customerId;
	private List<CartLine> cartLines;
}
