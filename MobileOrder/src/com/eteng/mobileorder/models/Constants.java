package com.eteng.mobileorder.models;

public class Constants {

	/**
	 * 服务器接口地址
	 */
	public static final String HOST_HEAD = "http://222.86.191.71:8080/cms/webservice/rest/lfAppService/";
	
	/**
	 * WS查询商户相关的菜单接口名称
	 */
	public static final String MENU_BY_ID = "queryMenuBySellerId";
	
	/**
	 * WS查询菜单相关菜品接口名称
	 */
	public static final String GOODS_BY_ID = "queryGoodsBySellerId";
	
	/**
	 * 分页查询中页数属性
	 */
	public static final String PAGE = "1";
	
	/**
	 * 分页查询中每页数量属性
	 */
	public static final String PAGE_COUNT = "20";
	
	/**
	 * 测试id
	 */
	public static final String SELLER_ID = "1";
	
	/**
	 * 配餐完成返回小票列表数据表示符
	 */
	public static final String DISH_COMBO_RESULT = "dish_combo_result";
	
	public static int REQUEST_CODE = 0x1223;
	public static int RESULT_CODE = 0x1225;
}
