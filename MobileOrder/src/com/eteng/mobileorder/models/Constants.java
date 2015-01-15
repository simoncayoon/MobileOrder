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
	 * 订单详情查询接口名称
	 */
	public static final String ORDER_BY_ORDERID = "queryOrderByOrderId";
	
	/**
	 * WS订单查询接口名称
	 */
	public static final String ORDER_BY_ID = "queryOrderBySellerId";
	
	/**
	 * 历史订单查询类型
	 */
	public static final String ORDER_QUERY_TYPE_HISTORY = "1";
	
	/**
	 * 微信订单查询类型
	 */
	public static final String ORDER_QUERY_TYPE_WX = "2";
	
	/**
	 * 当日订单查询类型
	 */
	public static final String ORDER_QUERY_TYPE_DAY = "3";
	
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
	
	public static String PRINT_FUNC_SWITCH = "print_func_switch";
}
