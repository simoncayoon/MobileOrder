package com.eteng.mobileorder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDetailModel implements Parcelable {
	
	/**
	 * 备注
	 */
	private String askFor;
	/**
	 * 打折价
	 */
	private double goodsDiscountPrice;
	/**
	 * 单价
	 */
	private double goodsSinglePrice;
	/**
	 * 附加组合
	 */
	private String attachName;
	/**
	 * 附加总价
	 */
	private double attachPrice;
	/**
	 * 订单ID
	 */
	private String orderId;
	/**
	 * 主餐名称
	 */
	private String goodsName;
	/**
	 * 订单总价
	 */
	private double totalPrice;
	/**
	 * 主食产品ID
	 */
	private int goodsId;
	/**
	 * 名称集合
	 */
	private String comboName;
	/**
	 * 备注集合
	 */
	private String remarkName;

	/**
	 * 构造方法
	 */
	public OrderDetailModel() {

	}

	public String getAskFor() {
		return askFor;
	}

	public void setAskFor(String askFor) {
		this.askFor = askFor;
	}

	public double getGoodsDiscountPrice() {
		return goodsDiscountPrice;
	}

	public void setGoodsDiscountPrice(double goodsDiscountPrice) {
		this.goodsDiscountPrice = goodsDiscountPrice;
	}

	public double getGoodsSinglePrice() {
		return goodsSinglePrice;
	}

	public void setGoodsSinglePrice(double goodsSinglePrice) {
		this.goodsSinglePrice = goodsSinglePrice;
	}

	public String getAttachName() {
		return attachName;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}

	public double getAttachPrice() {
		return attachPrice;
	}

	public void setAttachPrice(double attachPrice) {
		this.attachPrice = attachPrice;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public String getComboName() {
		return comboName;
	}

	public void setComboName(String comboName) {
		this.comboName = comboName;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.askFor);
		dest.writeDouble(this.goodsDiscountPrice);
		dest.writeDouble(this.goodsSinglePrice);
		dest.writeString(this.attachName);
		dest.writeDouble(this.attachPrice);
		dest.writeString(this.orderId);
		dest.writeString(this.goodsName);
		dest.writeDouble(this.totalPrice);
		dest.writeInt(this.goodsId);
		dest.writeString(this.comboName);
		dest.writeString(this.remarkName);
	}

	public static final Parcelable.Creator<OrderDetailModel> CREATOR = new Creator<OrderDetailModel>() {
		public OrderDetailModel createFromParcel(Parcel source) {
			return new OrderDetailModel(source);
		}

		@Override
		public OrderDetailModel[] newArray(int size) {
			return new OrderDetailModel[size];
		}
	};

	private OrderDetailModel(Parcel in) {
		this.askFor = in.readString();
		this.goodsDiscountPrice = in.readDouble();
		this.goodsSinglePrice = in.readDouble();
		this.attachName = in.readString();
		this.attachPrice = in.readDouble();
		this.orderId = in.readString();
		this.goodsName = in.readString();
		this.totalPrice = in.readDouble();
		this.goodsId = in.readInt();
		this.comboName = in.readString();
		this.remarkName = in.readString();
	}
}
