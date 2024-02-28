package com.bharath.cart.service.classes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bharath.cart.controller.mscommunication.ProductService;
import com.bharath.cart.entity.Cart;
import com.bharath.cart.entity.CartProductDetails;
import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.AddToCartProductRequest;
import com.bharath.cart.model.ViewProductResponse;
import com.bharath.cart.repository.CartProductRepository;
import com.bharath.cart.repository.CartRepository;
import com.bharath.cart.service.interfaces.CartService;
import com.bharath.cart.utility.CartServiceExceptionMessages;

import jakarta.transaction.Transactional;

@Service(value = "cartServiceImpl")
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartProductRepository cartProductRepository;

	@Autowired
	private ProductService productService;

	@Override
	@Transactional(rollbackOn = CartServiceException.class)
	public void addToCart(AddToCartProductRequest product, Long userId) throws CartServiceException {

		Cart cart = cartRepository.findById(userId).

				orElseGet(() -> {
					Cart c = new Cart();
					c.setCartId(userId);
					c.setCartValue(0f);
					c.setNoOfProductsInCart(0);
					c.setLastModifiedAt(null);
					return cartRepository.save(c);

				});

		ViewProductResponse productInfo = productService.viewProduct(product.getProductId());

		if (productInfo == null)
			throw new CartServiceException(CartServiceExceptionMessages.PRODUCT_INFO_NOT_AVAILABLE);
		Optional<CartProductDetails> cartProduct = cartProductRepository.findByCartCartIdAndProductId(userId,
				product.getProductId());

		if (cartProduct.isPresent())
			throw new CartServiceException(CartServiceExceptionMessages.ALREADY_ADDED);

		CartProductDetails cartProductDetails = new CartProductDetails();
		cartProductDetails.setProductId(product.getProductId());
		cartProductDetails.setCartProductQuantity(product.getQuantity());
		cartProductDetails.setCartProductName(productInfo.getProductName());
		cartProductDetails.setCartProductPrice(productInfo.getPrice());

		final Float subTotal = calculateSubtotal(productInfo.getPrice(), product.getQuantity(),
				productInfo.getDiscount());
		cartProductDetails.setSubTotal(subTotal);
		cart.setNoOfProductsInCart(cart.getNoOfProductsInCart() + 1);
		cart.setCartValue(cart.getCartValue() + subTotal);
		cart.setLastModifiedAt(LocalDateTime.now());
		cartProductDetails.setCart(cart);
		cartProductRepository.save(cartProductDetails);
	}

	private Float calculateSubtotal(Float price, Integer quantity, Float discount) {
	    
	    Float subtotal = price * quantity * (1 - (discount / 100));
	    return subtotal;
	}


}
