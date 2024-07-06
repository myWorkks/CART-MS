package com.bharath.cart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.AddToCartProductRequest;
import com.bharath.cart.model.ViewCartProductResponse;
import com.bharath.cart.model.ViewCartResponse;
import com.bharath.cart.service.interfaces.CartService;
import com.bharath.cart.utility.CartServiceConstants;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Validated
@Slf4j
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping(value = "cart/add")

	public ResponseEntity<String> addToCart(@RequestBody AddToCartProductRequest product,

			@RequestParam(required = true, name = "userId") String userId) throws CartServiceException {

		cartService.addToCart(product, Long.valueOf(userId));

		return new ResponseEntity<String>(CartServiceConstants.CART_ADD_SUCCESS, HttpStatus.CREATED);
	}

	@GetMapping(value = "cart/view")
	public ResponseEntity<ViewCartResponse> viewCart(@RequestParam(required = true, name = "cartId") Long cartId)
			throws CartServiceException {
		// System.out.println("view cart called");
		return new ResponseEntity<ViewCartResponse>(cartService.viewCart(cartId), HttpStatus.OK);
	}

	@GetMapping(value = "cart/view-id")
	public ResponseEntity<ViewCartProductResponse> viewCartProduct(
			@RequestParam(required = true, name = "cartProductId") Long cartProductId) throws CartServiceException {

		return new ResponseEntity<ViewCartProductResponse>(cartService.viewCartProduct(cartProductId), HttpStatus.OK);
	}

	@GetMapping(value = "cart/view-ids")
	public ResponseEntity<List<ViewCartProductResponse>> viewCartProductWithIds(
			@RequestParam(required = true, name = "cartProductIds") List<Long> cartProductIds)
			throws CartServiceException {

		return new ResponseEntity<List<ViewCartProductResponse>>(cartService.viewCartProductWithIds(cartProductIds),
				HttpStatus.OK);
	}

	@DeleteMapping(value = "cart/delete")
	public ResponseEntity<ViewCartResponse> deleteProductFromCart(
			@RequestParam(required = true, name = "cartId") Long cartId,
			@RequestParam(required = true, name = "cartProductId") Long cartProductId) throws CartServiceException {

		return new ResponseEntity<ViewCartResponse>(
				cartService.deleteProductFromCart(Long.valueOf(cartId), Long.valueOf(cartProductId)), HttpStatus.OK);
	}

	@DeleteMapping(value = "cart/delete-ids")
	public ResponseEntity<ViewCartResponse> deleteProductsFromCart(@RequestParam(required = true, name = "cartId") Long cartId,
			@RequestParam(required = true, name = "cartProductIds") List<Long> cartProductId)
			throws CartServiceException {

		return new ResponseEntity<ViewCartResponse>(cartService.deleteProductsFromCart(cartId, cartProductId), HttpStatus.OK);
	}

	@PutMapping(value = "cart/update")
	public ResponseEntity<ViewCartResponse> updateCart(@RequestParam(required = true, name = "cartId") Long cartId,
			@RequestParam(required = true, name = "cartProductId") Long cartProductId,
			@RequestParam(required = true, name = "quantity") Integer quantity,
			@RequestParam(required=false,name="isChecked")Boolean isChecked) throws CartServiceException {
		return new ResponseEntity<ViewCartResponse>(cartService.updateCart(cartId, cartProductId, quantity,isChecked),
				HttpStatus.OK);
	}
	@PutMapping(value = "cart/update-all")
	public ResponseEntity<ViewCartResponse> updateAllCartProducts(@RequestParam(required = true, name = "cartId") Long cartId,
			@RequestParam(required=false,name="isChecked")Boolean isChecked) throws CartServiceException {
		return new ResponseEntity<ViewCartResponse>(cartService.updateAllCartProducts(cartId,isChecked),
				HttpStatus.OK);
	}
}
