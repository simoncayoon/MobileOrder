package com.eteng.mobileorder.utils;

import java.util.ArrayList;

import android.content.Context;

import com.eteng.mobileorder.models.OrderDetailModel;
import com.eteng.mobileorder.models.OrderInfoModel;

public class PrintHelper {

	private Context mContext;
	private OrderInfoModel mOrderInfo;

	public PrintHelper(Context ctx) {
		this.mContext = ctx;
	}

	public String getPrintString(OrderInfoModel headerInfo,
			ArrayList<OrderDetailModel> dishCombo) {
		String headerString = getHeadString(headerInfo);
		String orderListString = getOrderList(dishCombo);
		return headerString + orderListString;
	}

	private String getHeadString(OrderInfoModel orderInfo) {
		mOrderInfo = orderInfo;
		String headerString = "";
		String orderId = "订单编号:" + orderInfo.getOrderSn() + "\n";
		String tel = "电话：" + orderInfo.getOrderTel() + "\n";
		String date = "时间：" + orderInfo.getCreateTime() + "\n";
		String addr = "地址：" + orderInfo.getAddress() + "\n";
		headerString = orderId + tel + date + addr + "\r\n";
		return headerString;
	}

	private String getOrderList(ArrayList<OrderDetailModel> dataSrc) {
		if (!(dataSrc.size() > 0)) {
			return "";
		}
		String printString = "";
		StringBuilder sb = new StringBuilder();
		// sb.append(getHeadString());
		for (OrderDetailModel item : dataSrc) {
			String temp = "";
			temp = "配餐：" + item.getComboName() + "\n" + "小计："
					+ item.getTotalPrice() + "\n" + "备注：" + item.getAskFor()
					+ "\r\n\r\n";
			sb.append(temp);
		}
		String footing = "合计：" + "\t\t\t\t" + mOrderInfo.getTotalPay() + "元"
				+ "\r\n\r\n";
		sb.append(footing);
		sb.append("\r\n\r\n\r\n\r\n");

		printString = sb.toString();
		return printString;
	}
}
