package com.eteng.mobileorder.models;

public class RemarkModel {

	private int id;
	private int categoryId;
	private String remarkName;
	private int orderInList;
	private String status;
	private int sellarId;
	private boolean isSelectStat;// 设置默认值

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public boolean isSelectStat() {
		return isSelectStat;
	}

	public void setSelectStat(boolean isSelectStat) {
		this.isSelectStat = isSelectStat;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getOrderInList() {
		return orderInList;
	}

	public void setOrderInList(int orderInList) {
		this.orderInList = orderInList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getSellarId() {
		return sellarId;
	}

	public void setSellarId(int sellarId) {
		this.sellarId = sellarId;
	}
}
