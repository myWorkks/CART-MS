package com.bharath.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bharath.cart.entity.CartProductDetails;

public interface CartProductRepository  extends JpaRepository<CartProductDetails, Long>{

	CartProductDetails findByProductId(Long productId);

	List<CartProductDetails> findByCartCartId(Long userId);

	Optional<CartProductDetails> findByCartCartIdAndProductId(Long userId, Long productId);

	

	CartProductDetails findByCartProductIdAndCartCartId(Long cartProductId, Long cartId);

}
