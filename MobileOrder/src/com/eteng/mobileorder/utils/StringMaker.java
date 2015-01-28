package com.eteng.mobileorder.utils;

import java.util.ArrayList;

public class StringMaker {

	/**
	 * 将字符数组用特东符号连接
	 * @param symbol
	 * @param src
	 * @return 组合成功的字符串
	 */
	public static String divWithSymbol(String symbol, ArrayList<String> src) {
		String target = "";
		StringBuilder sb = new StringBuilder();
		int count = src.size();
		if(count == 0){
			return "";
		}
		for (int i = 0; i < count; i++) {
			if (i < (count - 1)) {
				sb = sb.append(src.get(i) + symbol);
				continue;
			}
			sb = sb.append(src.get(i));
		}
		target = sb.toString();
		return target;
	}
}
