package com.eteng.mobileorder.cusomview;

import java.util.LinkedList;
import java.util.List;

import com.eteng.mobileorder.debug.DebugFlags;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class RemarkItemContainer extends LinearLayout implements RemarkListInterface{

	private static final String TAG = "RemarkItemContainer";
	
	private RemarkNameAdapter mAdapter;
	private OnItemSelectedListener onItemSelectedListener;
	private int mSelectedRemarkIndex = -1;
	
	public RemarkItemContainer(Context context) {
		super(context);
	}

	@Override
	public void onItemSelectListener() {
		
	}

	@Override
	public void setAdapter(RemarkNameAdapter adapter) {
		if (this.mAdapter != null) {
			this.mAdapter.unRegistDataSetObserver(dataSetObserver);
		}
		this.mAdapter = adapter;
		adapter.registDataSetObserver(dataSetObserver);
		adapter.notifyDataSetChanged();
	}

	@Override
	public RemarkNameAdapter getAdapter() {
		// TODO Auto-generated method stub
		return mAdapter;
	}
	
	private List<ViewGroup> views = new LinkedList<ViewGroup>();
	private DataSetObserver dataSetObserver = new DataSetObserver() {
		@Override
		public void onChange() {
			int count = getChildCount();
			int newCount = mAdapter.getCount();
			views.clear();
			for (int i = 0; i < count && i < newCount; i++) {
				views.add((ViewGroup) getChildAt(i));
			}
			removeAllViews();
			int size = views.size();
			for (int i = 0; i < newCount; i++) {
				LinearLayout result = new LinearLayout(getContext());
				View view;
				if (i < size) {
					View temp = views.get(i).getChildAt(0);
					views.get(i).removeView(temp);
					view = mAdapter.getView(i, temp, result);
				} else {
					view = mAdapter.getView(i, null, result);
				}
				result.addView(view);
				result.setOnClickListener(onClickListener);
				result.setTag(i);
				addView(result, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			}
		}
	};

	@Override
	public void setOnItemSelectListener(
			OnItemSelectedListener onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
		
	}
	
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int i = (Integer) v.getTag();
			ViewGroup parent = (ViewGroup) v;
			if (onItemSelectedListener != null) {
				onItemSelectedListener.onItemSelected(parent.getChildAt(0), i);
			}
		}
	};
}
