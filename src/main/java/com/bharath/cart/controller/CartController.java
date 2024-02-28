package com.bharath.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.AddToCartProductRequest;
import com.bharath.cart.service.interfaces.CartService;
import com.bharath.cart.utility.CartServiceConstants;

@RestController
@CrossOrigin
@Validated
public class CartController {

	
	@Autowired
	private CartService cartService;
	
	@PostMapping(value="cart/add")
	public ResponseEntity<String> addToCart(@RequestBody AddToCartProductRequest product,@RequestParam(required = true,name = "userId") Long userId) throws CartServiceException{
		cartService.addToCart(product, userId);
		
		return new ResponseEntity<String>(CartServiceConstants.CART_ADD_SUCCESS,HttpStatus.CREATED);
	}
}
