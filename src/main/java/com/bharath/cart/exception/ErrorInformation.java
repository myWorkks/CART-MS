package com.bharath.cart.exception;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorInformation {
	private Integer errorCode;
	private String errorMessage;
	private LocalDateTime occuredAt;

	


}
