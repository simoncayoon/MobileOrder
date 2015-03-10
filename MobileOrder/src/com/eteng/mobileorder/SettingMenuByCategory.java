package com.eteng.mobileorder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
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
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuItemModel;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.TempDataManager;
import com.eteng.mobileorder.utils.MyClickListener.PutAction;
import com.eteng.mobileorder.utils.NetController;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;

public class SettingMenuByCategory extends ListActivity implements
		NaviBtnListener, OnItemClickListener, OnItemLongClickListener,
		DropListener, View.OnClickListener, PutAction {

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

	}

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 */
	public DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.image_layout);
		controller.setRemoveEnabled(false);
		controller.setSortEnabled(false);
		controller.setDragInitMode(DragSortController.ON_DRAG);
		// controller.setRemoveMode(removeMode);
		return controller;
	}

	/***
	 * 获取相应种类下的所有数据
	 */
	private void getDataList() {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(SettingMenuByCategory.this,
				getResources().getString(R.string.toast_remind_loading), true,
				false, new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD + Constants.GOODS_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId",
				String.valueOf(TempDataManager.getInstance(this).getSellerId()));
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
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressHUD.dismiss();
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
			item.setId(temp.getInt("goodsId"));
			item.setDiscountPrice(temp.getDouble("discountPrice"));
			item.setImgUrl(temp.getString("goodsImgPath"));
			item.setPrice(temp.getDouble("goodsPrice"));
			item.setName(temp.getString("goodsName"));
			item.setType(temp.getString("goodsType"));
			item.setStatus(temp.getString("goodsStatus"));
			dataList.add(item);
		}
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {
		if (isEditState) {
			Intent mIntent = new Intent(SettingMenuByCategory.this,
					SettingDishImgUpload.class);
			mIntent.putExtra("CATEGORY_ID", categoryId);
			startActivity(mIntent);
		} else {
			isEditState = true;
			topBar.setRightBtnText("新增");
			mController.setRemoveMode(DragSortController.FLING_REMOVE);
			mController.setSortEnabled(true);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		showDeleteDialog(position,
				getResources().getString(R.string.dialog_dish_delete_text));
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void drop(int from, int to) {
		updateRemoteSort(from, to);
	}

	@Override
	public void onClick(View v) {
		showDialog();
	}

	void showDeleteDialog(final int position, String promptText) {
		Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setTitle("请选择状态");
		// 0: 默认第一个单选按钮被选中
		TextView promptView = new TextView(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		promptView.setLayoutParams(layoutParams);
		promptView.setGravity(Gravity.CENTER);
		promptView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		promptView.setText(promptText);
		builder.setView(promptView);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				postdishDelete(position);
			}

		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		// 创建一个单选按钮对话框
		builder.create().show();
	}

	private void postdishDelete(final int position) {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(SettingMenuByCategory.this,
				getResources().getString(R.string.toast_remind_deleting), true,
				false, new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});

		String url = Constants.HOST_HEAD + Constants.DISH_DELETE;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("goodsId",
				String.valueOf(dataList.get(position).getId()));
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								showToast(getResources().getString(
										R.string.toast_remind_delete_succeed));
								dataList.remove(position);
								mAdapter.notifyDataSetChanged();
							} else {
								showToast(getResources().getString(
										R.string.toast_remind_delete_failed));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						showToast(getResources().getString(
								R.string.toast_remind_delete_failed));
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);

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
										&& !newName.equals(menuName)) {// 不一样的内容
									// 提交新的类目名称
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
		builder.appendQueryParameter(
				"sellerId",
				String.valueOf(TempDataManager.getInstance(this).getSellerId()));
		builder.appendQueryParameter("classId", String.valueOf(categoryId));
		builder.appendQueryParameter("className", newName);
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
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
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dataList.clear();
		getDataList();
	}

	@Override
	public void postDishState(int position) {
		MenuItemModel item = dataList.get(position);
		String postState = "";
		if (item.getStatus().equals("1")) {// 将进行下架操作
			postState = "2";
		} else {// 将进行上架操作
			postState = "1";
		}
		postDishShownState(postState, position);
	}

	/**
	 * 提交修改后的显示状态 0，下架；1， 上架
	 */
	private void postDishShownState(final String status, final int position) {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(SettingMenuByCategory.this,
				getResources().getString(R.string.toast_remind_loading), true,
				false, new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD
				+ Constants.CHANGE_CATEGORY_SHOWN_STATUS;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter(
				"sellerId",
				String.valueOf(TempDataManager.getInstance(this).getSellerId()));
		builder.appendQueryParameter("type", Constants.SHOWN_TYPE_DISH);
		builder.appendQueryParameter("goodsId",
				String.valueOf(dataList.get(position).getId()));
		builder.appendQueryParameter("status", status);
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								showToast(getResources().getString(
										R.string.toast_remind_commit_succeed));
								dataList.get(position).setStatus(status);
								mAdapter.notifyDataSetChanged();
							}
						} catch (JSONException e) {
							showToast(getResources().getString(
									R.string.toast_remind_commit_failed));
							e.printStackTrace();
						}
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						showToast(getResources().getString(
								R.string.toast_remind_commit_failed));
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);

	}

	void showToast(String content) {
		Toast.makeText(SettingMenuByCategory.this, content, Toast.LENGTH_SHORT)
				.show();
	}

	protected void updateRemoteSort(final int from, final int to) {

		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(SettingMenuByCategory.this,
				getResources().getString(R.string.toast_remind_commiting),
				true, false, new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD + Constants.SORT_DISH;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("downGoodsId",
				String.valueOf(dataList.get(to).getId()));
		builder.appendQueryParameter("downGoodsOrder", String.valueOf(to));
		builder.appendQueryParameter("upGoodsId",
				String.valueOf(dataList.get(from).getId()));
		builder.appendQueryParameter("upGoodsOrder", String.valueOf(from));
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								MenuItemModel temp = dataList.get(from);
								dataList.remove(from);
								dataList.add(to, temp);
								mAdapter.notifyDataSetChanged();
								showToast(getResources().getString(
										R.string.toast_remind_commit_succeed));
							} else {
								showToast(getResources().getString(
										R.string.toast_remind_commit_failed));
							}
						} catch (JSONException e) {
							showToast(getResources().getString(
									R.string.toast_remind_commit_failed));
							e.printStackTrace();
						}
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						showToast(getResources().getString(
								R.string.toast_remind_commit_failed));
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}
}
