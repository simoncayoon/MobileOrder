package com.eteng.mobileorder.cusomview;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eteng.mobileorder.R;

public class CustomEditDialog extends Dialog {

	TextView titleView;
	EditText itemEdit;
	Button positiveBtn, cancelBtn;

	public CustomEditDialog(Context context) {
		super(context, R.style.CustomDialog);
		setCustomDialog();
	}

	private void setCustomDialog() {
		View mView = LayoutInflater.from(getContext()).inflate(
				R.layout.alert_dialog_edit_layout, null);
		titleView = (TextView) mView.findViewById(R.id.add_edit_title);
		itemEdit = (EditText) mView.findViewById(R.id.add_category_edit_text);
		positiveBtn = (Button) mView.findViewById(R.id.add_edit_query_btn);
		cancelBtn = (Button) mView.findViewById(R.id.add_edit_cancel_btn);
		super.setContentView(mView);
	}

	public View getEditText() {
		return itemEdit;
	}

	@Override
	public void setContentView(int layoutResID) {
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
	}

	@Override
	public void setContentView(View view) {
	}

	/**
	 * 确定键监听器
	 * 
	 * @param listener
	 */
	public void setOnPositiveListener(View.OnClickListener listener) {
		positiveBtn.setOnClickListener(listener);
	}

	/**
	 * 取消键监听器
	 * 
	 * @param listener
	 */
	public void setOnNegativeListener(View.OnClickListener listener) {
		cancelBtn.setOnClickListener(listener);
	}
}
