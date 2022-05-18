package com.swa.application.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShoppingCart {
    private String shoppingCartNumber;
	private List<CartLine> cartLines;
}
