package com.missionsky.scp.entity;

public class Product {
	private int id;
	private String partNumber;
	private String productName;
	private String productLink;
	private String promotionPrice;
	private String netPrice;
	private String vendorName;
	private String productCatagory;
	private String productParam;
	private String productImage;
	private String storeId;
	private String catalogId;
	private String vendor ;
	
	public Product(){};
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPromotionPrice() {
		return promotionPrice;
	}

	public void setPromotionPrice(String promotionPrice) {
		this.promotionPrice = promotionPrice;
	}

	public String getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(String netPrice) {
		this.netPrice = netPrice;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getProductCatagory() {
		return productCatagory;
	}

	public void setProductCatagory(String productCatagory) {
		this.productCatagory = productCatagory;
	}

	

	public String getProductParam() {
		return productParam;
	}


	public void setProductParam(String productParam) {
		this.productParam = productParam;
	}


	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getProductLink() {
		return productLink;
	}

	public void setProductLink(String productLink) {
		this.productLink = productLink;
	}
	
	
}
