package com.eteng.mobileorder.models;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItemModel implements Parcelable {
	/**
	 * 产品ID
	 */
	private int id;

	/**
	 * 产品串号
	 */
	private String serial;

	/**
	 * 菜名
	 */
	private String name;

	/**
	 * 价格
	 */
	private double price;

	/**
	 * 打折价
	 */
	private double discountPrice;

	/**
	 * 菜品图片地址
	 */
	private String imgUrl;

	/**
	 * 类型(主食或附加等)
	 */
	private String type;

	/**
	 * 状态（上架下架）
	 */
	private String status;

	/**
	 * 所属类目ID
	 */
	private int ownId;

	/**
	 * 备注信息
	 */
	private ArrayList<String> remark = new ArrayList<String>();

	/**
	 * 选择状态
	 */
	private boolean choiceState = false;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.serial);
		dest.writeString(this.name);
		dest.writeDouble(this.price);
		dest.writeDouble(this.discountPrice);
		dest.writeString(this.imgUrl);
		dest.writeString(this.type);
		dest.writeString(this.status);
		dest.writeInt(this.ownId);
		dest.writeList(remark);
		dest.writeByte(this.choiceState ? (byte) 1 : (byte) 0);
	}

	public static final Parcelable.Creator<MenuItemModel> CREATOR = new Creator<MenuItemModel>() {
		public MenuItemModel createFromParcel(Parcel source) {
			return new MenuItemModel(source);
		}

		@Override
		public MenuItemModel[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MenuItemModel[size];
		}
	};

	@SuppressWarnings("unchecked")
	private MenuItemModel(Parcel in) {
		this.id = in.readInt();
		this.serial = in.readString();
		this.name = in.readString();
		this.price = in.readDouble();
		this.discountPrice = in.readDouble();
		this.imgUrl = in.readString();
		this.type = in.readString();
		this.status = in.readString();
		this.ownId = in.readInt();
		this.remark = in.readArrayList(String.class.getClassLoader());
		this.choiceState = (in.readByte() == 1 ? true : false);
	}

	public MenuItemModel() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getOwnId() {
		return ownId;
	}

	public void setOwnId(int ownId) {
		this.ownId = ownId;
	}

	public ArrayList<String> getRemark() {
		return remark;
	}

	public void setRemark(ArrayList<String> remark) {
		this.remark = remark;
	}

	public boolean isChoiceState() {
		return choiceState;
	}

	public void setChoiceState(boolean choiceState) {
		this.choiceState = choiceState;
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
