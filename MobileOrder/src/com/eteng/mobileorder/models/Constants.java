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
	 * WS查询菜品类目下的备注信息接口名称
	 */
	public static final String OPTION_REMARK = "querySellerClassOption";
	
	/**
	 * 订单详情查询接口名称
	 */
	public static final String ORDER_BY_ORDERID = "queryOrderByOrderId";
	
	/**
	 * WS订单查询接口名称
	 */
	public static final String ORDER_BY_ID = "queryOrderBySellerId";
	
	/**
	 * WS新增菜品种类接口名称
	 */
	public static final String ADD_CATEGORY_BY_ID = "addClassBySellerId";
	
	/**
	 * WS修改类目名字接口名称
	 */
	public static final String UPDATE_CATEGORY_NAME = "updateClassNameById";
	
	/**
	 * WS登陆接口名称
	 */
	public static final String LOGIN = "login";
	
	/**
	 * WS提交订单接口名称
	 */
	public static final String COMMIT_ORDER_INFO = "addOrder";
	
	/**
	 * WS修改订单状态接口名称
	 */
	public static final String UPDATE_ORDER_STATUS = "updateOrderStatus";
	
	/**
	 * WS查询用户信息接口名称
	 */
	public static final String QUERY_CUSTOMER_INFO = "querySellerCustomerById";
	
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
	 * 菜单类别排序接口名称
	 */
	public static final String MENU_SORT = "changeClassOrder";
	
	public static final String EDIT_PROFILE = "editSellerInfo";
	
	/**
	 * 测试id
	 */
	public static final String SELLER_ID = "1";
	
	/**
	 * 配餐完成返回小票列表数据表示符
	 */
	public static final String DISH_COMBO_RESULT = "dish_combo_result";
	
	public static final String DISH_COMBO_RESULT_ATTACH = "dish_combo_result_attach";
	
	public static int REQUEST_CODE = 0x1223;
	public static int RESULT_CODE = 0x1225;
	
	public static String PRINT_FUNC_SWITCH = "print_func_switch";
	
	
	/**
	 * SharePerference 我的资料文件名称
	 */
	public static final String SP_OWN_PROFILE_NAME = "own_profile_name";
	
	/**
	 * SharePerference 通用文件名
	 */
	public static final String SP_GENERAL_PROFILE_NAME = "own_profile_name";
}
