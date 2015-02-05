package com.eteng.mobileorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.cusomview.RemarkListInterface.OnItemSelectedListener;
import com.eteng.mobileorder.cusomview.RemarkListInterface.RemarkNameAdapter;
import com.eteng.mobileorder.cusomview.RemarkListView;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.RemarkModel;
import com.eteng.mobileorder.utils.JsonPostRequest;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;

public class RemarkEditActivity extends Activity implements NaviBtnListener {

	private static final String TAG = RemarkEditActivity.class.getSimpleName();
	private static final String FLAG_UPDATE = "com.eteng.flag.update";
	private static final String FLAG_ADD = "com.eteng.flag.add";

	private TopNavigationBar topBar;
	private RemarkListView remarkListView;
	private Button menuName;

	private RemarkAdapter mAdapter;
	private ArrayList<RemarkModel> remarkList;
	private String noodleName = "";
	private String categoryId = "";
	private ProgressHUD mProgressHUD;
	private String sellerId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_remark_info_layout);
		sellerId = getSharedPreferences(Constants.SP_GENERAL_PROFILE_NAME,
				Context.MODE_PRIVATE).getString(Constants.SELLER_ID, "");
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		topBar.setTitle("备注信息");
		topBar.setLeftImg(R.drawable.setting_back_btn_bg);
		menuName = (Button) findViewById(R.id.remark_edit_menu_name);
		remarkListView = (RemarkListView) findViewById(R.id.remark_edit_list_view);
		remarkListView.setOnItemSelectListener(new OnItemSelectedListener() {// 备注选项信息
					@Override
					public void onItemSelected(View selectItemView, int select) {
						String tempName = "";
						String flag = "";
						if (select < (remarkList.size())) {// 修改备注名称
							tempName = remarkList.get(select).getRemarkName();
							flag = FLAG_UPDATE;
						} else {
							flag = FLAG_ADD;
						}
						showDialog(tempName, flag, select);
					}
				});
		remarkList = new ArrayList<RemarkModel>();
		getMenuCategory();// 获取粉面类目的名称
	}

	/**
	 * 获取菜单类型
	 */
	void getMenuCategory() {
		mProgressHUD = ProgressHUD.show(RemarkEditActivity.this, getResources()
				.getString(R.string.toast_remind_loading), true, false,
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD + Constants.MENU_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId", sellerId);// 测试ID，以后用shareperference保存
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								getMenuList(respon);
								getOptions();// 获取备注列表
							} else {

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

	void getMenuList(JSONObject JsonString) {
		try {
			JSONArray jsonArray = new JSONArray(
					JsonString.getString("classList"));
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject tmp = new JSONObject(jsonArray.getString(i));
				if (tmp.getString("isNoodle").equals("1")) {
					noodleName = tmp.getString("className");
					menuName.setText(noodleName);
					categoryId = tmp.getString("classId");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 获取相应种类下的备注数据
	 */
	private void getOptions() {
		String url = Constants.HOST_HEAD + Constants.OPTION_REMARK;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId", sellerId);// 测试ID，以后用shareperference保存
		builder.appendQueryParameter("classId", categoryId);
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {

						try {
							if (respon.getString("code").equals("0")) {// 查询成功
								JSONArray options = new JSONArray(
										respon.getString("optionList"));
								if (!(options.length() > 0)) {
									return;
								}
								remarkList.clear();
								for (int i = 0; i < options.length(); i++) {
									JSONObject temp = options.getJSONObject(i);
									RemarkModel item = new RemarkModel();
									item.setCategoryId(temp.getInt("classId"));
									item.setId(temp.getInt("id"));
									item.setOrderInList(temp
											.getInt("optionOrder"));
									item.setRemarkName(temp
											.getString("optionName"));
									item.setSellarId(temp.getInt("sellerId"));
									item.setStatus(temp
											.getString("optionStatus"));
									item.setSelectStat(false);// 默认不选中任何备注
									remarkList.add(item);
								}

								remarkListView.setVisibility(View.VISIBLE);
								mAdapter = new RemarkAdapter();
								remarkListView.setAdapter(mAdapter);
								mAdapter.notifyDataSetChanged();
							} else {
								try {
									throw new VolleyError();
								} catch (VolleyError e) {
									e.printStackTrace();
								}
							}
							mProgressHUD.dismiss();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressHUD.dismiss();
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {

	}

	public void showDialog(final String tempName, final String flag,
			final int position) {
		final Context context = this;
		// 定义1个文本输入框
		final EditText remarkEdit = new EditText(this);
		remarkEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				2) });
		remarkEdit.setText(tempName);
		// 创建对话框
		new AlertDialog.Builder(context).setTitle("请输入备注名称")// 设置对话框标题
				.setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图标
				.setView(remarkEdit)// 为对话框添加要显示的组件
				.setPositiveButton("确定", new OnClickListener() {// 设置对话框[肯定]按钮
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String newName = remarkEdit.getText()
										.toString();

								if (!newName.equals(tempName)) {// 不一样的内容
									// 提交新的类目名称
									if (!flag.equals(FLAG_UPDATE)) {// newName
																	// 不能添加空项
										if (newName.length() == 0) {
											return;
										}
									}
									postRemarkName(newName, flag, position);
								}
								return;
							}
						}).setNegativeButton("取消", null)// 设置对话框[否定]按钮
				.show();
	}

	private void postRemarkName(String newName, String flag, int position) {
		mProgressHUD.show();
		String url = "";

		if (flag.equals(FLAG_UPDATE)) {
			String urlHead = Constants.HOST_HEAD
					+ Constants.UPDATE_REMARK_BY_CATEGORY;
			Uri.Builder builder = Uri.parse(urlHead).buildUpon();
			builder.appendQueryParameter("optionName", newName);
			builder.appendQueryParameter("sellerId", sellerId);
			builder.appendQueryParameter("classId", categoryId);
			builder.appendQueryParameter("id",
					String.valueOf(remarkList.get(position).getId()));
			url = builder.toString();
			updateRemark(url);
		} else {
			Map<String, String> params = new HashMap<String, String>();
			params.put("optionName", newName);
			params.put("sellerId", sellerId);
			params.put("classId", categoryId);
			url = Constants.HOST_HEAD + Constants.ADD_REMARK_BY_CATEGORY;
			addNewRemark(url, params);
		}

	}

	void updateRemark(String url) {
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								Toast.makeText(RemarkEditActivity.this,
										"更改成功！", Toast.LENGTH_SHORT).show();
								getOptions();// 刷新列表
							} else {
								Toast.makeText(RemarkEditActivity.this, "更改失败",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
						Toast.makeText(RemarkEditActivity.this, "更改失败",
								Toast.LENGTH_SHORT).show();
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getMenuRequest, TAG);
	}

	void addNewRemark(String url, Map<String, String> params) {
		JsonPostRequest getOrderInfoRequest = new JsonPostRequest(
				Request.Method.POST, url, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								Toast.makeText(RemarkEditActivity.this,
										"添加成功!", Toast.LENGTH_SHORT).show();
								getOptions();// 刷新列表
							} else {
								Toast.makeText(RemarkEditActivity.this,
										"提交失败!", Toast.LENGTH_SHORT).show();
								mProgressHUD.dismiss();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							mProgressHUD.dismiss();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

						Toast.makeText(RemarkEditActivity.this,
								"VolleyError 提交失败!", Toast.LENGTH_SHORT).show();
						mProgressHUD.dismiss();
					}
				}, params);
		NetController.getInstance(getApplicationContext()).addToRequestQueue(
				getOrderInfoRequest, TAG);
	}

	class RemarkAdapter extends RemarkNameAdapter {

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public Object getItem(int position) {
			return remarkList.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.remark_edit_item_btn, null);
				holder.remarkName = (Button) convertView
						.findViewById(R.id.remark_edit_btn);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();
			if (position < remarkList.size()) {
				holder.remarkName.setText(remarkList.get(position)
						.getRemarkName());

			} else {
				holder.remarkName.setText("");
			}

			return convertView;
		}

	}

	class ViewHolder {
		Button remarkName;
	}
}
