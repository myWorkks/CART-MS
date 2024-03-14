package com.bharath.cart.service.interfaces;

import java.util.List;

import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.AddToCartProductRequest;
import com.bharath.cart.model.ViewCartProductResponse;
import com.bharath.cart.model.ViewCartResponse;

public interface CartService {
public void addToCart(AddToCartProductRequest product,Long userId) throws CartServiceException;

public ViewCartResponse viewCart(Long cartId) throws CartServiceException;

public void deleteProductFromCart(Long cartId, Long cartProductId) throws CartServiceException;

public ViewCartResponse updateCart(Long cartId, Long cartProductId, Integer quantity) throws CartServiceException;

public ViewCartProductResponse viewCartProduct(Long cartProductId) throws CartServiceException;

public List<ViewCartProductResponse> viewCartProductWithIds(List<Long> cartProductIds) throws CartServiceException;

public void deleteProductsFromCart(Long cartId, List<Long> cartProductId) throws CartServiceException;


}
