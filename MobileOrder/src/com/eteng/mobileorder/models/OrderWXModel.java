package com.eteng.mobileorder.models;

public class OrderWXModel {

	// "orderId": 262,
	// "orderSn": "LF1421052744480333874",
	// "sellerUserId": 1,
	// "buyerUserId": 0,
	// "totalPay": 762,
	// "shouldPay": 762,
	// "orderSource": "2",
	// "orderStatus": "2",
	// "createTime": "2015-01-12 16:52:24",
	// "createPerson": "1",
	// "besureTime": "2015-01-12 16:52:24",
	// "besurePerson": "1",
	// "addressId": 133,
	// "orderTel": "15222890000",
	// "orderAddress": "兴关路21号",
	// "orderOpenId": ""

	private int orderId;
	private String orderSn;
	private int sellerUserId;
	private int buyerUserId;
	private double totalPay;
	private double shouldPay;
	private String orderSource;
	private String orderStatus;
	private String createTime;
	private int addressId;
	private String orderTel;
	private String orderAddress;
	private String orderOpenId;
	

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	public int getSellerUserId() {
		return sellerUserId;
	}

	public void setSellerUserId(int sellerUserId) {
		this.sellerUserId = sellerUserId;
	}

	public int getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(int buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	public double getTotalPay() {
		return totalPay;
	}

	public void setTotalPay(double totalPay) {
		this.totalPay = totalPay;
	}

	public double getShouldPay() {
		return shouldPay;
	}

	public void setShouldPay(double shouldPay) {
		this.shouldPay = shouldPay;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getAddressId() {
		return addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}

	public String getOrderTel() {
		return orderTel;
	}

	public void setOrderTel(String orderTel) {
		this.orderTel = orderTel;
	}

	public String getOrderAddress() {
		return orderAddress;
	}

	public void setOrderAddress(String orderAddress) {
		this.orderAddress = orderAddress;
	}

	public String getOrderOpenId() {
		return orderOpenId;
	}

	public void setOrderOpenId(String orderOpenId) {
		this.orderOpenId = orderOpenId;
	}

}
