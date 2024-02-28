package com.bharath.cart.service.interfaces;

import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.AddToCartProductRequest;

public interface CartService {
public void addToCart(AddToCartProductRequest product,Long userId) throws CartServiceException;
}
