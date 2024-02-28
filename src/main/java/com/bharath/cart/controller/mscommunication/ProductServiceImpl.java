package com.bharath.cart.controller.mscommunication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bharath.cart.model.ViewProductResponse;

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
				.retrieve().bodyToMono(ViewProductResponse.class).block();

//	RestTemplate restTemplate =new RestTemplate();
//return	restTemplate.getForEntity(PRODUCTSERVICE_URL+VIEW_PRODUCT_END_POINT+productId, ViewProductResponse.class).getBody();
	}

}
