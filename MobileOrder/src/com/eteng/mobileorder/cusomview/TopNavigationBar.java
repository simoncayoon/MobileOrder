package com.eteng.mobileorder.cusomview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eteng.mobileorder.R;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.utils.DisplayMetrics;

public class TopNavigationBar extends RelativeLayout {

	private static final String TAG = "TopNavigationBar";

	private Button leftBtn, rightBtn, tempBtn;
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
	
	private Context mContext;

	public TopNavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
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
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText(LeftString);
	}

	public void setRightBtnEnable() {
		// remove the old button (if there is one)   
        if (tempBtn != null){
        	return;
        }       
        Button newButton = new Button(mContext);
  
        // set OnClickListener  
        newButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallBack.rightBtnListener();				
			}
		});  
  
        // set LayoutParams  
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);  
        try{
        	lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); 
        } catch (IllegalArgumentException e){
        	DebugFlags.logD(TAG, e.getMessage());
        }
        lp.addRule(RelativeLayout.CENTER_VERTICAL);  
        lp.setMargins(10, 0, 10, 0);  
        newButton.setLayoutParams(lp);   
        newButton.setTextSize(DisplayMetrics.sp2px(mContext, 5));  
        newButton.setTextColor(Color.WHITE);  
  
        // set button drawable  
        newButton.setBackgroundColor(Color.parseColor("#00000000"));
        tempBtn = newButton;
        // add button  
        this.addView(tempBtn);  
	}
	
	public void setRightBtnText(String str){
		tempBtn.setText(str);
	}

	public void setLeftImg(int drawableId) {
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setBackgroundResource(drawableId);
	}
	
	@SuppressLint("NewApi")
	public void setLeftImg(Drawable drawableId) {
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setBackground(drawableId);
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
