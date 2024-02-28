package com.bharath.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.AddToCartProductRequest;
import com.bharath.cart.model.ViewCartResponse;
import com.bharath.cart.service.interfaces.CartService;
import com.bharath.cart.utility.CartServiceConstants;

@RestController
@CrossOrigin
@Validated
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping(value = "cart/add")
	public ResponseEntity<String> addToCart(@RequestBody AddToCartProductRequest product,
			@RequestParam(required = true, name = "userId") Long userId) throws CartServiceException {
		cartService.addToCart(product, userId);

		return new ResponseEntity<String>(CartServiceConstants.CART_ADD_SUCCESS, HttpStatus.CREATED);
	}

	@GetMapping(value = "cart/view")
	public ResponseEntity<ViewCartResponse> viewCart(@RequestParam(required = true, name = "cartId") Long cartId)
			throws CartServiceException {

		return new ResponseEntity<ViewCartResponse>(cartService.viewCart(cartId), HttpStatus.OK);
	}

	@DeleteMapping(value = "cart/delete")
	public ResponseEntity<String> deleteProductFromCart(@RequestParam(required = true, name = "cartId") Long cartId,
			@RequestParam(required = true, name = "cartProductId") Long cartProductId) throws CartServiceException {
		cartService.deleteProductFromCart(cartId, cartProductId);

		return new ResponseEntity<String>(CartServiceConstants.PRODUCT_DELETED_FROM_CART, HttpStatus.OK);
	}

	@PatchMapping(value = "cart/update")
	public ResponseEntity<ViewCartResponse> updateCart(@RequestParam(required = true, name = "cartId") Long cartId,
			@RequestParam(required = true, name = "cartProductId") Long cartProductId,
			@RequestParam(required = true, name = "quantity") Integer quantity) throws CartServiceException {
		return new ResponseEntity<ViewCartResponse>(cartService.updateCart(cartId, cartProductId, quantity),
				HttpStatus.OK);
	}
}
