package com.eteng.mobileorder;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		mDslv = (DragSortListView) findViewById(R.id.setting_upload_sort_list_view);
		mController = buildController(mDslv);
		mDslv.setFloatViewManager(mController);
		mDslv.setOnTouchListener(mController);
		mDslv.setDragEnabled(dragEnabled);
		setListAdapter();
	}

	ArrayAdapter<String> adapter;

	private String[] array;
	private ArrayList<String> list;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				String item = adapter.getItem(from);
				adapter.remove(item);
				adapter.insert(item, to);
			}
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			adapter.remove(adapter.getItem(which));
		}
	};
	//
	// protected int getLayout() {
	// // this DSLV xml declaration does not call for the use
	// // of the default DragSortController; therefore,
	// // DSLVFragment has a buildController() method.
	// return R.layout.dslv_fragment_main;
	// }

	/**
	 * Return list item layout resource passed to the ArrayAdapter.
	 */
	// protected int getItemLayout() {
	// /*if (removeMode == DragSortController.FLING_LEFT_REMOVE || removeMode ==
	// DragSortController.SLIDE_LEFT_REMOVE) {
	// return R.layout.list_item_handle_right;
	// } else */
	// if (removeMode == DragSortController.CLICK_REMOVE) {
	// return R.layout.list_item_click_remove;
	// } else {
	// return R.layout.list_item_handle_left;
	// }
	// }

	private DragSortListView mDslv;
	private DragSortController mController;

	public int dragStartMode = DragSortController.ON_DOWN;
	public boolean removeEnabled = false;
	public int removeMode = DragSortController.FLING_REMOVE;
	public boolean sortEnabled = true;
	public boolean dragEnabled = true;

	// public static DSLVFragment newInstance(int headers, int footers) {
	// DSLVFragment f = new DSLVFragment();
	//
	// Bundle args = new Bundle();
	// args.putInt("headers", headers);
	// args.putInt("footers", footers);
	// f.setArguments(args);
	//
	// return f;
	// }

	public DragSortController getController() {
		return mController;
	}

	/**
	 * Called from DSLVFragment.onActivityCreated(). Override to set a different
	 * adapter.
	 */
	public void setListAdapter() {
		array = new String[] { "111", "222", "333" };
		list = new ArrayList<String>(Arrays.asList(array));

		adapter = new ArrayAdapter<String>(this,
				R.layout.setting_upload_sort_list_item_layout,
				R.id.upload_menu_name, list);
		mDslv.setAdapter(adapter);
	}

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 */
	public DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.upload_menu_drag_handle);
		controller.setClickRemoveId(0);
		controller.setRemoveEnabled(removeEnabled);
		controller.setSortEnabled(sortEnabled);
		controller.setDragInitMode(dragStartMode);
		controller.setRemoveMode(removeMode);
		return controller;
	}

	/** Called when the activity is first created. */
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		mDslv = (DragSortListView) inflater.inflate(getLayout(), container,
//				false);
//
//		
//
//		return mDslv;
//	}

//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//
//		mDslv = (DragSortListView) getListView();
//
//		mDslv.setDropListener(onDrop);
//		mDslv.setRemoveListener(onRemove);
//
//		Bundle args = getArguments();
//		int headers = 0;
//		int footers = 0;
//		if (args != null) {
//			headers = args.getInt("headers", 0);
//			footers = args.getInt("footers", 0);
//		}
//
//		for (int i = 0; i < headers; i++) {
//			addHeader(getActivity(), mDslv);
//		}
//		for (int i = 0; i < footers; i++) {
//			addFooter(getActivity(), mDslv);
//		}
//
//		setListAdapter();
//	}

//	public static void addHeader(Activity activity, DragSortListView dslv) {
//		LayoutInflater inflater = activity.getLayoutInflater();
//		int count = dslv.getHeaderViewsCount();
//
//		TextView header = (TextView) inflater.inflate(R.layout.header_footer,
//				null);
//		header.setText("Header #" + (count + 1));
//
//		dslv.addHeaderView(header, null, false);
//	}
//
//	public static void addFooter(Activity activity, DragSortListView dslv) {
//		LayoutInflater inflater = activity.getLayoutInflater();
//		int count = dslv.getFooterViewsCount();
//
//		TextView footer = (TextView) inflater.inflate(R.layout.header_footer,
//				null);
//		footer.setText("Footer #" + (count + 1));
//
//		dslv.addFooterView(footer, null, false);
//	}
}
