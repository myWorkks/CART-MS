package com.bharath.cart.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ViewCartProductResponse {

	
	private String productName;
	private Float price;
	private Integer quantity;
	private Float subTotal;
	private Float discount;
	private Long cartProductId;
	private Long productId;
	private String imagePath;
	private Boolean isChecked;
}
