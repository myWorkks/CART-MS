package com.bharath.cart.controller.mscommunication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.bharath.cart.exception.CartServiceException;
import com.bharath.cart.model.ViewProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Service(value = "productService")
public class ProductServiceImpl implements ProductService {
	@Autowired
	
	private WebClient.Builder webclient;
	private static final String PRODUCTSERVICE_URL = "http://inventory-ms";

	private static final String VIEW_PRODUCT_END_POINT = "product-view";

	@Override
	public ViewProductResponse viewProduct(Long productId) {
		return webclient.baseUrl(PRODUCTSERVICE_URL).build().get()
				.uri(uriBuilder -> uriBuilder.path(VIEW_PRODUCT_END_POINT).queryParam("pid", productId).build())
				.retrieve().bodyToMono(ViewProductResponse.class).onErrorResume(WebClientResponseException.class, ex -> {
					String responseCode = String.valueOf(ex.getRawStatusCode());
					if (responseCode.startsWith("2"))
						return Mono.empty();
					else if (responseCode.equals("400")) {

						ObjectMapper objectMapper = new ObjectMapper();
						JsonNode jsonNode = null;
						try {
							jsonNode = objectMapper.readTree(ex.getResponseBodyAsString());
						} catch (JsonProcessingException e) {
							throw new RuntimeException(e);
						}
						String errorMessage = jsonNode.get("errorMessage").asText();
						return Mono.error(() -> new CartServiceException(errorMessage));
					}

				else
						return Mono.error(ex);
				}).block();

//	RestTemplate restTemplate =new RestTemplate();
//return	restTemplate.getForEntity(PRODUCTSERVICE_URL+VIEW_PRODUCT_END_POINT+productId, ViewProductResponse.class).getBody();
	}

}
