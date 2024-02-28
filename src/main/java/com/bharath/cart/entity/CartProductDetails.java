package com.bharath.cart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter

@ToString
public class CartProductDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartProductId;
	private String cartProductName;
	private Float cartProductPrice;
	private Integer cartProductQuantity;
	private Float subTotal;
	private Long productId;
	private LocalDateTime addedAt;
	private Float discount;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "cart_id", unique = false)
	private Cart cart;

}
