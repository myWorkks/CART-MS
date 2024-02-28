package com.bharath.cart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter

public class Cart {
@Id
private Long cartId;
private Integer noOfProductsInCart;
private Float cartValue;
private LocalDateTime lastModifiedAt; 

}
