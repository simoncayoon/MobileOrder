package com.eteng.mobileorder.cusomview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class RemarkListView extends HorizontalScrollView implements
		RemarkListInterface {

	private RemarkItemContainer itemContainer;

	@SuppressWarnings("unused")
	private static final String TAG = "RemarkListView";

	public RemarkListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		itemContainer = new RemarkItemContainer(context);
		addView(itemContainer, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT));
		setHorizontalScrollBarEnabled(false);
	}

	@Override
	public void onItemSelectListener() {

	}

	@Override
	public void setAdapter(RemarkNameAdapter adapter) {

		if (getAdapter() != null) {
			getAdapter().unRegistDataSetObserver(dataSetObserver);
		}
		itemContainer.setAdapter(adapter);
		adapter.registDataSetObserver(dataSetObserver);
	}

	@Override
	public RemarkNameAdapter getAdapter() {
		return itemContainer.getAdapter();
	}

	private DataSetObserver dataSetObserver = new DataSetObserver() {

		@Override
		public void onChange() {

		}
	};

	@Override
	public void setOnItemSelectListener(
			OnItemSelectedListener onItemSelectedListener) {
		itemContainer.setOnItemSelectListener(onItemSelectedListener);

	}
}
