package com.eteng.mobileorder.models;


import android.os.Parcel;
import android.os.Parcelable;

public class MenuItemModel implements Parcelable{

	// "goodsId": 6,
	// "goodsSerial": "1",
	// "goodsName": "豆腐炒肉套餐",
	// "goodProduction": "暂无简介",
	// "goodsPrice": 13,
	// "discountPrice": 13,
	// "goodsNumber": 9,
	// "goodsImgPath": "http://222.86.191.71:8080/cms/meal/tcl/dfcr.jpg",
	// "goodsType": "1",
	// "goodsStatus": "1",
	// "createTime": "2014-12-22 00:00:00",
	// "createPerson": "1",
	// "goodsOwnerId": 1,
	// "goodsClass": "2",
	// "goodsOrder": 9
	/**
	 * 菜品图片地址
	 */
	private String imgUrl;
	/**
	 * 菜名
	 */
	private String name;
	/**
	 * 打折价
	 */
	private double discountPrice;
	/**
	 * 价格
	 */
	private double itemPrice;
	/**
	 * 类型
	 */
	private String type;

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public double getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(double itemPrice) {
		this.itemPrice = itemPrice;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.imgUrl);
		dest.writeString(this.name);
		dest.writeString(this.type);
		dest.writeDouble(this.discountPrice);
		dest.writeDouble(this.itemPrice);
	}
	
	public static final Parcelable.Creator<MenuItemModel> CREATOR = new Creator<MenuItemModel>() { 
		  public MenuItemModel createFromParcel(Parcel source) { 
			  MenuItemModel newsInfo = new MenuItemModel(); 
		      newsInfo.name = source.readString(); 
		      newsInfo.imgUrl = source.readString(); 
		      newsInfo.type = source.readString(); 
		      newsInfo.discountPrice = source.readDouble();
		      newsInfo.itemPrice = source.readDouble();
		      return newsInfo; 
		  }

		@Override
		public MenuItemModel[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MenuItemModel[size];
		} 
	};

}
