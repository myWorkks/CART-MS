package com.bharath.cart.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewCartResponse {

	private Float cartValue;
	private Integer noOfProductsInCart;
	private List<ViewCartProductResponse> cartProducts;
	private PriceSummary priceSummary;
	

}
