package com.eteng.mobileorder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.adapter.MenuCategoryAdapter;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.MenuItemModel;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;

public class OrderPhoneFragment extends BaseFragment implements
		OnItemClickListener {

	private static final String TAG = "OrderPhoneFragment";
	private ProgressBar progressBar;
	private GridView mGridView;
	private int categoryId;
	private boolean isSingleSelect;
	private static final String KEY_LIST_POSITION = "key_list_position";
	public static final String INTENT_INT_CATEGORY_ID = "intent_int_category_id";
	public static final String INTENT_IS_NOODLE = "is_noodle";
	private int mFirstVisible;
	private ArrayList<MenuItemModel> dataList;
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int id) {
		}
	};
	private Callbacks mCallbacks = sDummyCallbacks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataList = new ArrayList<MenuItemModel>();
		categoryId = getArguments().getInt(INTENT_INT_CATEGORY_ID);// 类型ID
		isSingleSelect = getArguments().getBoolean(INTENT_IS_NOODLE);
	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		super.onCreateView(savedInstanceState);
		setContentView(R.layout.fragment_tabmain_item);
		DebugFlags.logD(TAG, "当前的种类ID：" + categoryId);
		progressBar = (ProgressBar) findViewById(R.id.fragment_mainTab_item_progressBar);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mGridView = (GridView) findViewById(R.id.asset_grid);
		if (isSingleSelect) {
			DebugFlags.logD(TAG, "粉面类, 单选选模式");
		} else {

		}
		mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		mGridView.setOnItemClickListener(this);

		if (savedInstanceState != null) {
			mFirstVisible = savedInstanceState.getInt(KEY_LIST_POSITION);
		}

		mGridView.setSelection(mFirstVisible);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
		setHasOptionsMenu(true);
		getDataList();
	}

	/***
	 * 获取相应种类下的所有数据
	 */
	private void getDataList() {
		String url = Constants.HOST_HEAD + Constants.GOODS_BY_ID;
		DebugFlags.logD(TAG, "this url is " + url);
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
								mGridView
										.setAdapter(new MenuCategoryAdapter<MenuItemModel>(
												getActivity()
														.getApplicationContext(),
												dataList,
												R.layout.header,
												R.layout.order_phone_item_category_layout));
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
						progressBar.setVisibility(View.GONE);
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

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			mGridView.setItemChecked(mActivatedPosition, false);
		} else {
			mGridView.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(int position);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mCallbacks.onItemSelected(position);
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mGridView
					.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
							: ListView.CHOICE_MODE_NONE);
		}
	}
}
