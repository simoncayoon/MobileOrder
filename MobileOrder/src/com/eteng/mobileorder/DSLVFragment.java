package com.eteng.mobileorder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.utils.JsonUTF8Request;
import com.eteng.mobileorder.utils.NetController;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class DSLVFragment extends ListFragment implements OnItemClickListener,
		OnItemLongClickListener{

	private static final String TAG = "DSLVFragment";

	ArrayAdapter<String> adapter;

	public ArrayList<String> menuArray = null;
	public ArrayList<Integer> menuIdArray = null;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {				
				String item = adapter.getItem(from);
				adapter.remove(item);
				adapter.insert(item, to);// 改变展示效果
				
				int changeId = menuIdArray.get(from);//修改menuID顺序
				menuIdArray.remove(from);
				menuIdArray.add(to, changeId);
				// updateRemoteSort();//远程从新排序
			}
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			adapter.remove(adapter.getItem(which));
		}
	};

	protected int getLayout() {
		// this DSLV xml declaration does not call for the use
		// of the default DragSortController; therefore,
		// DSLVFragment has a buildController() method.
		return R.layout.sort_list_view;
	}

	/**
	 * Return list item layout resource passed to the ArrayAdapter.
	 */
	protected int getItemLayout() {
		return R.layout.setting_upload_sort_list_item_layout;
	}

	private DragSortListView mDslv;
	private DragSortController mController;

	public int dragStartMode = DragSortController.ON_DRAG;
	public boolean removeEnabled = true;
	public int removeMode = DragSortController.FLING_REMOVE;
	public boolean sortEnabled = true;
	public boolean dragEnabled = true;

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
	public void setListAdapter() {
		adapter = new ArrayAdapter<String>(getActivity(), getItemLayout(),
				R.id.upload_menu_name, menuArray);
		setListAdapter(adapter);
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
		menuIdArray = new ArrayList<Integer>();
		mDslv = (DragSortListView) inflater.inflate(getLayout(), container,
				false);
		mController = buildController(mDslv);
		mDslv.setFloatViewManager(mController);
		mDslv.setOnTouchListener(mController);
		mDslv.setDragEnabled(dragEnabled);
		getListData();
		return mDslv;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mDslv = (DragSortListView) getListView();
		mDslv.setDropListener(onDrop);
		mDslv.setRemoveListener(onRemove);
		mDslv.setOnItemClickListener(this);
		mDslv.setOnItemLongClickListener(this);
		// setListAdapter();
	}

	private void getListData() {
		String url = Constants.HOST_HEAD + Constants.MENU_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("sellerId", Constants.SELLER_ID);// 测试ID，以后用shareperference保存
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						menuArray = getMenuList(respon);
						setListAdapter();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
					}
				});
		NetController.getInstance(getActivity().getApplicationContext())
				.addToRequestQueue(getMenuRequest, TAG);
	}

	ArrayList<String> getMenuList(JSONObject JsonString) {
		menuIdArray.clear();
		ArrayList<String> dataList = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(
					JsonString.getString("classList"));
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject tmp = new JSONObject(jsonArray.getString(i));
				dataList.add(tmp.getString("className"));
				menuIdArray.add(tmp.getInt("classId"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataList;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// remoteDeleteMenu();//远程删除
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent mIntent = new Intent(getActivity(), SettingMenuByCategory.class);
		mIntent.putExtra("test", menuIdArray.get(position));
		mIntent.putExtra("menu_name", menuArray.get(position));
		startActivity(mIntent);
	}
}
