package com.eteng.mobileorder.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.eteng.mobileorder.R;

public class MyClickListener implements OnClickListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = MyClickListener.class.getSimpleName();

	private static MyClickListener instance = null;
	private static PutAction mDummyCall = new PutAction() {

		@Override
		public void postDishState(int position) {
			// TODO Auto-generated method stub

		}
	};

	private static PutAction mCallBack = mDummyCall;

	private MyClickListener() {
	};

	public static MyClickListener getInstance(Context context) {
		if(context instanceof PutAction){
			mCallBack = (PutAction) context;
		}
		if (instance == null) {
			instance = new MyClickListener();
		}
		return instance;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag(R.id.KEY_POSITION);
		mCallBack.postDishState(position);
	}

	public interface PutAction {
		void postDishState(int position);
	}
}
