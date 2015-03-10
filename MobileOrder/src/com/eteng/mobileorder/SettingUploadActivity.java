package com.eteng.mobileorder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuCategoryModel;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.eteng.mobileorder.utils.TempDataManager;
import com.mobeta.android.dslv.DragSortController;

public class SettingUploadActivity extends FragmentActivity implements
		NaviBtnListener {

	private static final String TAG = "SettingUploadActivity";

	private TopNavigationBar topBar;

	private MenuCategoryModel menuCategory;
	private DSLVFragment fragment;

	private int mDragStartMode = DragSortController.ON_DRAG;
	private boolean mRemoveEnabled = false;
	private int mRemoveMode = DragSortController.MISS;
	private boolean mSortEnabled = false;
	private boolean mDragEnabled = false;
	private String mTag = "dslvTag";

	private boolean isEditStat = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_upload_layout);
		initView();
		initData();
		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.upload_fragment_container, getNewDslvFragment(),
							mTag).commit();
		}
	}

	private Fragment getNewDslvFragment() {
		DSLVFragment f = DSLVFragment.newInstance(0, 0);
		f.removeMode = mRemoveMode;
		f.removeEnabled = mRemoveEnabled;
		f.dragStartMode = mDragStartMode;
		f.sortEnabled = mSortEnabled;
		f.dragEnabled = mDragEnabled;
		fragment = f;
		return f;
	}

	/**
	 * 实例化视图控件
	 */
	private void initView() {
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		menuCategory = new MenuCategoryModel();
		topBar.setTitle("菜品编辑");
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);
		topBar.setRightBtnEnable();
		topBar.setRightBtnText("编辑");
	}

	@Override
	public void leftBtnListener() {
		finish();

	}

	@Override
	public void rightBtnListener() {
		DragSortController mController = fragment.getController();
		if (isEditStat) {// 弹出编辑框
			showDialog();
		} else {
			topBar.setRightBtnText("新增");
			isEditStat = true;
			mController.setDragInitMode(DragSortController.ON_DRAG);
			mController.setSortEnabled(true);// 开启滑动排序功能
			// mController.setRemoveEnabled(true);
			// mController.setRemoveMode(DragSortController.FLING_REMOVE);
			fragment.setTapbarAction();
		}
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
								if (newName.length() > 0) {
									for (MenuCategoryModel item : fragment.menuArray) {
										if (newName.equals(item
												.getCategoryName())) {// 判断已有列表是否有同样名称
											Toast.makeText(
													SettingUploadActivity.this,
													"已存在同样的类目！",
													Toast.LENGTH_SHORT).show();
											return;
										}
									}
									postCategoryName(newName);
								}
							}
						}).setNegativeButton("取消", null)// 设置对话框[否定]按钮
				.show();
	}

	private void postCategoryName(String newName) {
		String url = Constants.HOST_HEAD + Constants.ADD_CATEGORY_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId",
				String.valueOf(TempDataManager.getInstance(this).getSellerId()));
		builder.appendQueryParameter("className", newName);
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.POST, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								parseJsonSrc(new JSONObject(
										respon.getString("goodsClass")));
								fragment.menuArray.add(menuCategory);
								fragment.adapter.notifyDataSetChanged();
							} else {
								Toast.makeText(SettingUploadActivity.this,
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

	void parseJsonSrc(JSONObject json) throws JSONException {
		menuCategory.setCategoryCode(json.getString("classCode"));
		menuCategory.setCategoryId(json.getInt("classId"));
		menuCategory.setCategoryName(json.getString("className"));
		menuCategory.setCategoryOrder(json.getInt("classOrder"));
		menuCategory.setCreateDate(json.getString("createDate"));
		if (json.getString("isNoodle").equals("")) {
			menuCategory.setNoodle(false);
		} else if (json.getString("isNoodle").equals("1")) {
			menuCategory.setNoodle(true);
		}
		menuCategory.setSellerId(json.getInt("sellerId"));
	}
}
