package com.eteng.mobileorder.cusomview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.eteng.mobileorder.R;
import com.eteng.mobileorder.debug.DebugFlags;

public class TopNavigationBar extends FrameLayout {

	private static final String TAG = "TopNavigationBar";

	private Button leftBtn, rightBtn;
	private TextView titleView;
	private NaviBtnListener callBack = new NaviBtnListener() {

		@Override
		public void rightBtnListener() {
		}

		@Override
		public void leftBtnListener() {
		}
	};
	private NaviBtnListener mCallBack = callBack;

	public TopNavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(
				R.layout.general_navi_title_layout, this);
		titleView = (TextView) findViewById(R.id.general_navi_title_view);
		leftBtn = (Button) findViewById(R.id.general_navi_left_btn);
		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallBack.leftBtnListener();
			}
		});
		rightBtn = (Button) findViewById(R.id.general_navi_right_btn);
		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallBack.rightBtnListener();
			}
		});
		if (!(context instanceof NaviBtnListener)) {

		}
		if (context instanceof NaviBtnListener) {
			mCallBack = (NaviBtnListener) context;
		} else {

		}
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	public void setLeftBtnText(String LeftString) {
		leftBtn.setText(LeftString);
	}

	public void setRightBtnText(String rightString) {
		rightBtn.setText(rightString);
	}

	public void setLeftImg(int drawableId) {
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setBackgroundResource(drawableId);
	}

	public void setRightImg(int drawableId) {
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setBackgroundResource(drawableId);
	}

	public void setLeftBtnHide() {
		leftBtn.setVisibility(View.GONE);
	}

	public void setRightRight() {
		rightBtn.setVisibility(View.GONE);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mCallBack = callBack;
	}

	public interface NaviBtnListener {
		void leftBtnListener();

		void rightBtnListener();
	}
}
