package com.eteng.mobileorder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuCategoryModel;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class DSLVFragment extends ListFragment implements OnItemClickListener,
		OnItemLongClickListener {

	private static final String TAG = "DSLVFragment";
	private DragSortListView mDslv;
	private DragSortController mController;

	public int dragStartMode = DragSortController.ON_DRAG;
	public boolean removeEnabled = true;
	public int removeMode = DragSortController.FLING_REMOVE;
	public boolean sortEnabled = true;
	public boolean dragEnabled = true;

	public MenuOrderAdapter adapter = null;
	public ArrayList<MenuCategoryModel> menuArray = null;
	private boolean isEditable = false;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				updateRemoteSort(from, to);// 远程从新排序
			}
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {

		}
	};

	protected int getLayout() {
		return R.layout.sort_list_view;
	}

	protected void updateRemoteSort(final int from, final int to) {

		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(getActivity(), getResources()
				.getString(R.string.toast_remind_commiting), true, false,
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD + Constants.SORT_MENU;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("downClassId",
				String.valueOf(menuArray.get(to).getCategoryId()));
		builder.appendQueryParameter("downClassOrder", String.valueOf(to));
		builder.appendQueryParameter("upClassId",
				String.valueOf(menuArray.get(from).getCategoryId()));
		builder.appendQueryParameter("upClassOrder", String.valueOf(from));
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (respon.getString("code").equals("0")) {
								MenuCategoryModel temp = menuArray.get(from);
								menuArray.remove(from);
								menuArray.add(to, temp);
								adapter.notifyDataSetChanged();
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
		NetController.getInstance(getActivity().getApplicationContext())
				.addToRequestQueue(getMenuRequest, TAG);
	}

	void showToast(String content) {
		Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
	}

	public static DSLVFragment newInstance(int headers, int footers) {
		DSLVFragment f = new DSLVFragment();
		Bundle args = new Bundle();
		args.putInt("headers", headers);
		args.putInt("footers", footers);
		f.setArguments(args);

		return f;
	}

	public DragSortController getController() {
		return mController;
	}

	/**
	 * Called from DSLVFragment.onActivityCreated(). Override to set a different
	 * adapter.
	 */
	public void setMyAdapter() {
		adapter = new MenuOrderAdapter();
		mDslv.setAdapter(adapter);
	}

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 */
	public DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.upload_menu_drag_handle);
		controller.setRemoveEnabled(removeEnabled);
		controller.setSortEnabled(sortEnabled);
		controller.setDragInitMode(dragStartMode);
		controller.setRemoveMode(removeMode);
		return controller;
	}

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		menuArray = new ArrayList<MenuCategoryModel>();
		mDslv = (DragSortListView) inflater.inflate(getLayout(), container,
				false);
		mController = buildController(mDslv);
		mDslv.setFloatViewManager(mController);
		mDslv.setOnTouchListener(mController);
		mDslv.setDragEnabled(dragEnabled);
		return mDslv;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getListData();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mDslv = (DragSortListView) getListView();
		mDslv.setDropListener(onDrop);
		mDslv.setRemoveListener(onRemove);
		mDslv.setOnItemClickListener(this);

	}

	private void getListData() {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(getActivity(), getResources()
				.getString(R.string.toast_remind_loading), true, false,
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD + Constants.MENU_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter(
				"sellerId",
				getActivity()
						.getSharedPreferences(
								Constants.SP_GENERAL_PROFILE_NAME,
								Context.MODE_PRIVATE).getString(
								Constants.SP_SELLER_ID, ""));
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						getMenuList(respon);
						setMyAdapter();
						mProgressHUD.dismiss();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getActivity().getApplicationContext())
				.addToRequestQueue(getMenuRequest, TAG);
	}

	void getMenuList(JSONObject JsonString) {
		menuArray.clear();
		try {
			JSONArray jsonArray = new JSONArray(
					JsonString.getString("classList"));
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject tmp = new JSONObject(jsonArray.getString(i));
				MenuCategoryModel item = new MenuCategoryModel();
				item.setCategoryName(tmp.getString("className"));
				item.setCategoryId(tmp.getInt("classId"));
				item.setStatus(tmp.getString("classStatus"));
				menuArray.add(item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		String putState = "";
		String promptText = "";
		if (menuArray.get(position).getStatus().equals("1")) {
			putState = "0";// 下架状态
			promptText = getResources().getString(R.string.dialog_put_off_text);
		} else {
			putState = "1";// 上架状态
			promptText = getResources().getString(R.string.dialog_put_on_text);
		}
		showSelectDialog(position, putState, promptText);
		return true;
	}

	void showSelectDialog(final int position, final String state,
			String promptText) {
		Builder builder = new android.app.AlertDialog.Builder(getActivity());
		builder.setTitle("请选择状态");
		// 0: 默认第一个单选按钮被选中
		TextView promptView = new TextView(getActivity());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		promptView.setLayoutParams(layoutParams);
		promptView.setGravity(Gravity.CENTER);
		promptView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		promptView.setText(promptText);
		builder.setView(promptView);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				postCategoryShownState(state, position);
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

	/**
	 * 提交修改后的显示状态 0，下架；1， 上架
	 */
	private void postCategoryShownState(String status, int position) {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(getActivity(), getResources()
				.getString(R.string.toast_remind_loading), true, false,
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
		String url = Constants.HOST_HEAD
				+ Constants.CHANGE_CATEGORY_SHOWN_STATUS;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter(
				"sellerId",
				getActivity()
						.getSharedPreferences(
								Constants.SP_GENERAL_PROFILE_NAME,
								Context.MODE_PRIVATE).getString(
								Constants.SP_SELLER_ID, ""));
		builder.appendQueryParameter("type", Constants.SHOWN_TYPE_CATEGORY);
		builder.appendQueryParameter("classId",
				String.valueOf(menuArray.get(position).getCategoryId()));
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
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
						showToast(getResources().getString(
								R.string.toast_remind_commit_failed));
						mProgressHUD.dismiss();
					}
				});
		NetController.getInstance(getActivity().getApplicationContext())
				.addToRequestQueue(getMenuRequest, TAG);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent mIntent = new Intent(getActivity(), SettingMenuByCategory.class);
		mIntent.putExtra("test", menuArray.get(position).getCategoryId());
		mIntent.putExtra("menu_name", menuArray.get(position).getCategoryName());
		startActivity(mIntent);
	}

	public void setTapbarAction() {
		/**
		 * 显示拖图案、设置长按动作
		 */
		isEditable = true;
		adapter.notifyDataSetChanged();
		mDslv.setOnItemLongClickListener(this);
		mDslv.setDragEnabled(true);
	}

	class MenuOrderAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return menuArray.size();
		}

		@Override
		public Object getItem(int position) {
			return menuArray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.setting_upload_sort_list_item_layout, null);
				mHolder = new ViewHolder();
				mHolder.menuName = (TextView) convertView
						.findViewById(R.id.upload_menu_name);
				mHolder.dragHandle = (TextView) convertView
						.findViewById(R.id.upload_menu_drag_handle);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				convertView.setLayoutParams(params);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.menuName.setText(menuArray.get(position).getCategoryName());
			if (isEditable) {
				mHolder.dragHandle.setBackgroundColor(getResources().getColor(
						R.color.GENERAL_TEXT_COLOR));
				mHolder.dragHandle.setText(String.valueOf(position));
			}
			return convertView;
		}

		class ViewHolder {
			TextView menuName, dragHandle;
		}
	}
}
