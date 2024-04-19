package com.bharath.cart.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AddToCartProductRequest {
	private Long productId;

	
	private Integer quantity;
}
