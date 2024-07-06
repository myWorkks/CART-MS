package com.bharath.cart.service.classes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bharath.cart.controller.mscommunication.ProductService;
import com.bharath.cart.entity.Cart;
import com.bharath.cart.entity.CartProductDetails;
import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.AddToCartProductRequest;
import com.bharath.cart.model.PriceSummary;
import com.bharath.cart.model.ViewCartProductResponse;
import com.bharath.cart.model.ViewCartResponse;
import com.bharath.cart.model.ViewProductResponse;
import com.bharath.cart.repository.CartProductRepository;
import com.bharath.cart.repository.CartRepository;
import com.bharath.cart.service.interfaces.CartService;
import com.bharath.cart.utility.CartServiceConstants;
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
		if (product.getQuantity() > 10)
			throw new CartServiceException(CartServiceExceptionMessages.QUANTITY_NOT_ALLOWED);
		if (productInfo.getMinStockLevel() < product.getQuantity())
			throw new CartServiceException(CartServiceExceptionMessages.PRODUCT_NOT_AVAILABLE);
		CartProductDetails cartProductDetails = new CartProductDetails();
		cartProductDetails.setProductId(product.getProductId());
		cartProductDetails.setCartProductQuantity(product.getQuantity());
		cartProductDetails.setCartProductName(productInfo.getProductName());
		cartProductDetails.setCartProductPrice(productInfo.getPrice());
		cartProductDetails.setAddedAt(LocalDateTime.now());
		cartProductDetails.setDiscount(productInfo.getDiscount());
		cartProductDetails.setIsChecked(true);
		if (productInfo.getFiles() != null && !productInfo.getFiles().isEmpty()) {

			cartProductDetails.setCartProductImagePath(
					productInfo.getFiles().stream().filter(asset -> asset.isPrimary() && !asset.isVideo())
							.map(asset -> asset.getFilePath()).findFirst().orElse(null));
			;
		}
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

	@Override
	public ViewCartResponse viewCart(Long cartId) throws CartServiceException {
		Optional<Cart> optionalCart = cartRepository.findById(cartId);
		Cart cart = optionalCart
				.orElseThrow(() -> new CartServiceException(CartServiceExceptionMessages.CART_NOT_FOUND));

		List<CartProductDetails> cartProducts = cartProductRepository.findByCartCartId(cartId);
		if (cart.getNoOfProductsInCart() == 0 || cartProducts.isEmpty())
			throw new CartServiceException(CartServiceExceptionMessages.NO_PRODUCT_IN_CART);
		return mapToViewCartResponse(cart, cartProducts);
	}

	private ViewCartResponse mapToViewCartResponse(Cart cart, List<CartProductDetails> cartProducts) {
		ViewCartResponse viewCartResponse = new ViewCartResponse();
		viewCartResponse.setCartValue(cart.getCartValue());
		viewCartResponse.setNoOfProductsInCart(cart.getNoOfProductsInCart());
		viewCartResponse.setPriceSummary(mapPriceSummary(cartProducts));
		viewCartResponse.setCartProducts(cartProducts.stream().map(cartProd -> {

			return mapToViewCartProductResponse(cartProd);
		}).toList());
		return viewCartResponse;
	}

	private PriceSummary mapPriceSummary(List<CartProductDetails> cartProducts) {
		PriceSummary priceSummary = new PriceSummary();

		Double totalMRP = cartProducts.stream().filter(CartProductDetails::getIsChecked)
				.mapToDouble(product -> product.getCartProductPrice() * product.getCartProductQuantity()).sum();

		Double totalAmount = cartProducts.stream().filter(CartProductDetails::getIsChecked)
				.mapToDouble(CartProductDetails::getSubTotal).sum();

		Double totalDiscount = totalMRP - totalAmount;

		priceSummary.setTotalMRP(totalMRP);
		priceSummary.setTotalDiscount(totalDiscount);
		priceSummary.setTotalAmount(totalAmount);
		priceSummary.setNoOfCheckItems((int) cartProducts.stream().filter(CartProductDetails::getIsChecked).count());
		return priceSummary;
	}

	private ViewCartProductResponse mapToViewCartProductResponse(CartProductDetails cartProd) {
		ViewCartProductResponse viewCartProductResponse = new ViewCartProductResponse();
		viewCartProductResponse.setQuantity(cartProd.getCartProductQuantity());
		viewCartProductResponse.setSubTotal(cartProd.getSubTotal());
		viewCartProductResponse.setProductName(cartProd.getCartProductName());
		viewCartProductResponse.setPrice(cartProd.getCartProductPrice());
		viewCartProductResponse.setDiscount(cartProd.getDiscount());
		viewCartProductResponse.setCartProductId(cartProd.getCartProductId());
		viewCartProductResponse.setProductId(cartProd.getProductId());
		viewCartProductResponse.setImagePath(cartProd.getCartProductImagePath());
		viewCartProductResponse.setIsChecked(cartProd.getIsChecked() == null ? false : cartProd.getIsChecked());
		return viewCartProductResponse;

	}

	@Override
	public ViewCartResponse deleteProductFromCart(Long cartId, Long cartProductId) throws CartServiceException {
		CartProductDetails cartProduct = cartProductRepository.findByCartProductIdAndCartCartId(cartProductId, cartId);
		if (cartProduct == null)
			throw new CartServiceException(CartServiceExceptionMessages.PRODUCT_NOT_AVAILABLE_IN_CART);
		cartProduct.setCart(null);
		cartProductRepository.delete(cartProduct);
		Optional<Cart> optCart = cartRepository.findById(cartId);
		Cart cart = optCart.orElseThrow(() -> new CartServiceException(CartServiceExceptionMessages.CART_NOT_FOUND));
		cart.setCartValue(cart.getCartValue() - cartProduct.getSubTotal());
		cart.setNoOfProductsInCart(cart.getNoOfProductsInCart() - 1);
		cart.setLastModifiedAt(LocalDateTime.now());
		cart = cartRepository.save(cart);
		return viewCart(cartId);
	}

	@Override
	public ViewCartResponse updateCart(Long cartId, Long cartProductId, Integer quantity, Boolean isChecked)
			throws CartServiceException {
		CartProductDetails cartProduct = cartProductRepository.findByCartProductIdAndCartCartId(cartProductId, cartId);
		if (cartProduct == null)
			throw new CartServiceException(CartServiceExceptionMessages.PRODUCT_NOT_AVAILABLE_IN_CART);
		Optional<Cart> optCart = cartRepository.findById(cartId);
		Cart cart = optCart.orElseThrow(() -> new CartServiceException(CartServiceExceptionMessages.CART_NOT_FOUND));

		if (quantity == 0)
			throw new CartServiceException(CartServiceExceptionMessages.UPDATE_QUANTITY_ZERO);

		ViewProductResponse productInfo = productService.viewProduct(cartProduct.getProductId());

		if (productInfo == null)
			throw new CartServiceException(CartServiceExceptionMessages.PRODUCT_INFO_NOT_AVAILABLE);
		if (quantity > 10)
			throw new CartServiceException(CartServiceExceptionMessages.QUANTITY_NOT_ALLOWED);
		if (productInfo.getMinStockLevel() < quantity)
			throw new CartServiceException(CartServiceExceptionMessages.PRODUCT_NOT_AVAILABLE);
		cart.setCartValue(cart.getCartValue() - cartProduct.getSubTotal());
		final Float subTotal = calculateSubtotal(cartProduct.getCartProductPrice(), quantity,
				cartProduct.getDiscount());
		cartProduct.setCartProductQuantity(quantity);
		cartProduct.setSubTotal(subTotal);
		if (isChecked != null)
			cartProduct.setIsChecked(isChecked);
		cart.setCartValue(cart.getCartValue() + subTotal);

		cart.setLastModifiedAt(LocalDateTime.now());
		cartProduct.setCart(cart);
		cartProductRepository.save(cartProduct);
		return viewCart(cartId);
	}

	@Override
	public ViewCartProductResponse viewCartProduct(Long cartProductId) throws CartServiceException {
		Optional<CartProductDetails> optionalCartProduct = cartProductRepository.findById(cartProductId);

		CartProductDetails cartProduct = optionalCartProduct
				.orElseThrow(() -> new CartServiceException(CartServiceExceptionMessages.CART_PRODUCT_NOT_FOUND));

		return mapToViewCartProductResponse(cartProduct);
	}

	@Override
	public List<ViewCartProductResponse> viewCartProductWithIds(List<Long> cartProductIds) throws CartServiceException {
		List<CartProductDetails> cartProductDetails = cartProductRepository.findAllById(cartProductIds);

		if (cartProductDetails.isEmpty())
			throw new CartServiceException(CartServiceExceptionMessages.CART_PRODUCT_NOT_FOUND);

		return cartProductDetails.stream().map(cartProduct -> mapToViewCartProductResponse(cartProduct)).toList();
	}

	@Override
	public ViewCartResponse deleteProductsFromCart(Long cartId, List<Long> cartProductIds) throws CartServiceException {

//		List<CartProductDetails> cartProductDetails = cartProductRepository.findByCartCartIdAndCartProductIdIn(cartId,
//				cartProductIds);
//		if (cartProductDetails != null && cartProductDetails.isEmpty()) {
//			throw new CartServiceException(CartServiceExceptionMessages.PRODUCT_NOT_AVAILABLE_IN_CART);
//		} else if (cartProductDetails != null && cartProductDetails.size() == 1) {
//			cartProductRepository.delete(cartProductDetails.get(0));
//		} else {
//			cartProductRepository.deleteAll(cartProductDetails);
		// }
		for (Long cartProductId : cartProductIds) {
			deleteProductFromCart(cartId, cartProductId);
		}

		return viewCart(cartId);
	}

	@Override
	public ViewCartResponse updateAllCartProducts(Long cartId, Boolean isChecked) throws CartServiceException {
		List<CartProductDetails> cartProducts = cartProductRepository.findByCartCartId(cartId);
		if (cartProducts == null || cartProducts.isEmpty())
			throw new CartServiceException(CartServiceExceptionMessages.PRODUCT_NOT_AVAILABLE_IN_CART);
		Optional<Cart> optCart = cartRepository.findById(cartId);
		Cart cart = optCart.orElseThrow(() -> new CartServiceException(CartServiceExceptionMessages.CART_NOT_FOUND));
		cart.setLastModifiedAt(LocalDateTime.now());
		cartProducts = cartProducts.stream().map(cartproduct -> {
			cartproduct.setIsChecked(isChecked);
			cartproduct.setCart(cart);
			return cartproduct;
		}).collect(Collectors.toList());

		cartProductRepository.saveAll(cartProducts);
		return viewCart(cartId);
	}

}
