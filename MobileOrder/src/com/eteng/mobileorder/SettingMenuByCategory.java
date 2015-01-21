package com.eteng.mobileorder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.adapter.SettingMenuCategoryAdapter;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuItemModel;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;

public class SettingMenuByCategory extends ListActivity implements
		NaviBtnListener, OnItemClickListener, OnItemLongClickListener,
		DropListener, View.OnClickListener {

	private static final String TAG = "SettingMenuByCategory";

	private TextView nameView;
	private DragSortListView contentListView;
	private TopNavigationBar topBar;
	private LinearLayout categoryEdit;

	private SettingMenuCategoryAdapter mAdapter;
	private ArrayList<MenuItemModel> dataList = null;
	DragSortController mController = null;
	private int categoryId;
	private String menuName = "";
	
	private boolean isEditState = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_menu_by_category_layout);
		categoryId = getIntent().getIntExtra("test", -1);
		menuName = getIntent().getStringExtra("menu_name");
		initView();
		initData();
	}

	private void initView() {
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);
		topBar.setRightBtnEnable();
		topBar.setRightBtnText("编辑");
		categoryEdit = (LinearLayout) findViewById(R.id.menu_category_edit_layout);
		categoryEdit.setOnClickListener(this);
		nameView = (TextView) findViewById(R.id.menu_by_category_name);
		contentListView = (DragSortListView) getListView();
		contentListView.setOnItemClickListener(this);
		contentListView.setOnItemLongClickListener(this);

		mController = buildController(contentListView);
		contentListView.setFloatViewManager(mController);
		contentListView.setOnTouchListener(mController);
		contentListView.setDragEnabled(true);
		contentListView.setDropListener(this);
	}

	private void initData() {
		dataList = new ArrayList<MenuItemModel>();
		topBar.setTitle(menuName);
		nameView.setText(menuName);
		getDataList();
	}

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 */
	public DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.image_layout);
		controller.setRemoveEnabled(false);
		controller.setSortEnabled(true);
		controller.setDragInitMode(DragSortController.ON_DRAG);
		// controller.setRemoveMode(removeMode);
		return controller;
	}

	/***
	 * 获取相应种类下的所有数据
	 */
	private void getDataList() {
		String url = Constants.HOST_HEAD + Constants.GOODS_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId", Constants.SELLER_ID);// 测试ID，以后用shareperference保存
		builder.appendQueryParameter("goodsClass", String.valueOf(categoryId));
		builder.appendQueryParameter("page", Constants.PAGE);
		builder.appendQueryParameter("pageCount", Constants.PAGE_COUNT);
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {// 查询成功
								String jsonString = respon
										.getString("goodsList");
								parseJson(jsonString);
								mAdapter = new SettingMenuCategoryAdapter(
										SettingMenuByCategory.this, dataList);
								contentListView.setAdapter(mAdapter);
							} else {
								try {
									throw new VolleyError();
								} catch (VolleyError e) {
									e.printStackTrace();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	/**
	 * 将JSON字符串数据解析到链表
	 * 
	 * @param jsonString
	 * @throws JSONException
	 */
	protected void parseJson(String jsonString) throws JSONException {

		JSONArray srcList = new JSONArray(jsonString);
		for (int i = 0; i < srcList.length(); i++) {
			JSONObject temp = new JSONObject(srcList.getString(i));
			MenuItemModel item = new MenuItemModel();
			item.setDiscountPrice(temp.getDouble("discountPrice"));
			item.setImgUrl(temp.getString("goodsImgPath"));
			item.setItemPrice(temp.getDouble("goodsPrice"));
			item.setName(temp.getString("goodsName"));
			item.setType(temp.getString("goodsType"));
			dataList.add(item);
		}
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {
		if(isEditState){
			
		} else {
			topBar.setRightBtnText("新增");
			mController.setRemoveMode(DragSortController.FLING_REMOVE);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		DebugFlags.logD(TAG, "onItemLongClick");
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DebugFlags.logD(TAG, "onItemClick");
	}

	@Override
	public void drop(int from, int to) {

		MenuItemModel temp = dataList.get(from);
		dataList.remove(from);
		dataList.add(to, temp);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		showDialog();
	}

	public void showDialog() {
		final Context context = this;
		// 定义1个文本输入框
		final EditText categoryEdit = new EditText(this);
		// 创建对话框
		new AlertDialog.Builder(context).setTitle("请输入菜品类名")// 设置对话框标题
				.setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图标
				.setView(categoryEdit)// 为对话框添加要显示的组件
				.setPositiveButton("确定", new OnClickListener() {// 设置对话框[肯定]按钮
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String newName = categoryEdit.getText()
										.toString();
								if (newName.length() > 0
										&& !newName.equals(menuName)) {//不一样的内容
									//提交新的类目名称
									postCategoryName(newName);
								} else if (newName.length() == 0) {
									Toast.makeText(SettingMenuByCategory.this,
											"不能为空！", Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton("取消", null)// 设置对话框[否定]按钮
				.show();
	}
	
	private void postCategoryName(final String newName) {
		String url = Constants.HOST_HEAD + Constants.UPDATE_CATEGORY_NAME;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId", Constants.SELLER_ID);// 测试ID，以后用shareperference保存
		builder.appendQueryParameter("classId", String.valueOf(categoryId));
		builder.appendQueryParameter("className", newName);
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						DebugFlags.logD(TAG, respon.toString());
						try {
							if (respon.getString("code").equals("0")) {
								menuName = newName;
								topBar.setTitle(menuName);
								nameView.setText(menuName);
							} else {
								Toast.makeText(SettingMenuByCategory.this,
										"提交失败", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}
}
