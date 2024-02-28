package com.bharath.cart.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddToCartProductRequest {
	private Long productId;

	private Float price;
	private Integer quantity;
}
