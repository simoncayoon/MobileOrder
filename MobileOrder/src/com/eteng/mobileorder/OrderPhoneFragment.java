package com.eteng.mobileorder;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.eteng.mobileorder.adapter.MenuCategoryAdapter;
import com.eteng.mobileorder.adapter.RemarkListAdapter;
import com.eteng.mobileorder.cusomview.RemarkListInterface.OnItemSelectedListener;
import com.eteng.mobileorder.cusomview.RemarkListView;
import com.eteng.mobileorder.models.DishInfo;
import com.eteng.mobileorder.models.RemarkInfo;
import com.eteng.mobileorder.utils.DbHelper;

public class OrderPhoneFragment extends BaseFragment implements
		OnItemClickListener {

	@SuppressWarnings("unused")
	private static final String TAG = "OrderPhoneFragment";
	private static final String KEY_LIST_POSITION = "key_list_position";
	public static final String INTENT_INT_CATEGORY_ID = "intent_int_category_id";
	public static final String INTENT_IS_NOODLE = "is_noodle";

	private GridView mGridView;
	private RemarkListView remarkListView;

	public Long categoryId;
	public boolean isSingleSelect;
	private int mFirstVisible;
	private ArrayList<DishInfo> mainList;
	private ArrayList<DishInfo> attachList;
	private ArrayList<RemarkInfo> remarkList;
	public MenuCategoryAdapter<DishInfo> mAdapter;
	public RemarkListAdapter mRemarkAdapter;
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
		mainList = new ArrayList<DishInfo>();
		attachList = new ArrayList<DishInfo>();
		remarkList = new ArrayList<RemarkInfo>();
		categoryId = getArguments().getLong(INTENT_INT_CATEGORY_ID);// 类型ID
		isSingleSelect = getArguments().getBoolean(INTENT_IS_NOODLE);
	}

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		super.onCreateView(savedInstanceState);
		setContentView(R.layout.fragment_tabmain_item);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mGridView = (GridView) findViewById(R.id.asset_grid);
		remarkListView = (RemarkListView) findViewById(R.id.menu_remark_list);
		remarkListView.setOnItemSelectListener(new OnItemSelectedListener() {// 备注选项信息
					@Override
					public void onItemSelected(View selectItemView, int select) {
						mRemarkAdapter.setChoiceState(select);
					}
				});
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
		setDataAdapter();
	}

	private void setDataAdapter() {
		mainList = (ArrayList<DishInfo>) DbHelper.getInstance(getActivity()).getLocalDish(categoryId, "1");
		if (isSingleSelect) {
			attachList = (ArrayList<DishInfo>) DbHelper.getInstance(
					getActivity()).getLocalDish(categoryId, "");
			mAdapter = new MenuCategoryAdapter<DishInfo>(
					getApplicationContext(), mainList, attachList,
					R.layout.header, R.layout.order_phone_item_category_layout,
					isSingleSelect);
		} else {
			mAdapter = new MenuCategoryAdapter<DishInfo>(
					getApplicationContext(), mainList, R.layout.header,
					R.layout.order_phone_item_category_layout, false);
		}
		mGridView.setAdapter(mAdapter);
		
		remarkList = (ArrayList<RemarkInfo>) DbHelper.getInstance(getActivity()).getRemarkInfos(categoryId);
		if(remarkList.size() > 0){
			remarkListView.setVisibility(View.VISIBLE);
			mRemarkAdapter = new RemarkListAdapter(
					getActivity(), remarkList);
			remarkListView.setAdapter(mRemarkAdapter);
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
		// 将所选项添加到选择列表中
		mAdapter.setChoiceState(position);
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

	public MenuCategoryAdapter<DishInfo> getAdapter() {
		if (this.mAdapter == null) {
		
		}
		return mAdapter;
	}
	
	public boolean hasData(){
		return mainList.size() > 0 ? true : false;
	}
}
