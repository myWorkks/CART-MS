package com.bharath.cart.model;

import lombok.Data;

@Data
public class AssetViewResponse {
	private Long productId;
	private String filePath;
	private boolean isPrimary;
	private boolean isImage;
	private boolean isVideo;
	private Long assetId;
}
