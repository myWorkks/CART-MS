package com.bharath.cart.service.interfaces;

import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.AddToCartProductRequest;
import com.bharath.cart.model.ViewCartResponse;

public interface CartService {
public void addToCart(AddToCartProductRequest product,Long userId) throws CartServiceException;

public ViewCartResponse viewCart(Long cartId) throws CartServiceException;

public void deleteProductFromCart(Long cartId, Long cartProductId) throws CartServiceException;

public ViewCartResponse updateCart(Long cartId, Long cartProductId, Integer quantity) throws CartServiceException;
}
