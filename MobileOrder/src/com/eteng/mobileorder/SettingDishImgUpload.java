package com.eteng.mobileorder;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.eteng.mobileorder.utils.MultiPartJSONRequest;
import com.eteng.mobileorder.utils.MultiPartStack;

public class SettingDishImgUpload extends Activity implements NaviBtnListener,
		OnClickListener {

	private static final String TAG = "SettingDishImgUpload";
	private static final int REQUEST_EX = 1;
	private static final int DISH_TYPE_MAIN = 1;
	private static final int DISH_TYPE_APPEND = 2;

	private TextView previewBg;
	private ImageButton addBitMapBtn;
	private EditText dishNameEdit, dishPriceEdit;
	private Button commitBtn, typeSelector;
	private TopNavigationBar topBar;

	private int categoryId = -1;
	private Bitmap btMap = null;// 缓存图片
	private int CURRENT_SELECT_DISH_TYPE = -1;// 当前选中的菜品类型
	private RequestQueue mSingleQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_dish_upload_layout);
		mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
		initView();
		initData();
	}

	private void initData() {
		categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
		topBar.setTitle("菜品上传");
		topBar.setLeftBtnEnable();
		topBar.setLeftBtnText("取消");

		addBitMapBtn.setOnClickListener(this);
		typeSelector.setOnClickListener(this);
	}

	private void initView() {
		topBar = (TopNavigationBar) findViewById(R.id.general_navi_view);
		previewBg = (TextView) findViewById(R.id.camera_preview_back_view);
		Drawable cameraDrawable = getResources().getDrawable(
				R.drawable.camera_icon);
		cameraDrawable.setBounds(new Rect(0, 0,
				DisplayMetrics.dip2px(this, 50), DisplayMetrics
						.dip2px(this, 50)));
		previewBg.setCompoundDrawables(null, cameraDrawable, null, null);
		addBitMapBtn = (ImageButton) findViewById(R.id.add_camera_preview_img);
		dishNameEdit = (EditText) findViewById(R.id.dish_name_edit_view);
		dishPriceEdit = (EditText) findViewById(R.id.dish_price_edit_view);
		typeSelector = (Button) findViewById(R.id.category_type_selector);
		commitBtn = (Button) findViewById(R.id.general_commit_btn);
		commitBtn.setOnClickListener(this);
	}

	@Override
	public void leftBtnListener() {
		finish();
	}

	@Override
	public void rightBtnListener() {

	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == R.id.add_camera_preview_img) {// 添加照片或者相机
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQUEST_EX);
		}
		if (vId == R.id.category_type_selector) {// 选择菜品类型，弹出选择框
			showSelectDialog();
		}
		if (vId == R.id.general_commit_btn) {// 提交按钮
			if (btMap == null) {
				Toast.makeText(SettingDishImgUpload.this, "请选择菜品图片",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (dishNameEdit.getText().toString().length() == 0) {
				Toast.makeText(SettingDishImgUpload.this, "请输入菜名",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (dishPriceEdit.getText().toString().length() == 0) {
				Toast.makeText(SettingDishImgUpload.this, "请输入价格",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (CURRENT_SELECT_DISH_TYPE == -1) {
				Toast.makeText(SettingDishImgUpload.this, "请选择类型",
						Toast.LENGTH_SHORT).show();
				return;
			}

			postImgAction();// 发送表单信息
		}
	}

	private void postImgAction() {
		final ProgressHUD mProgressHUD;
		mProgressHUD = ProgressHUD.show(SettingDishImgUpload.this,
				getResources().getString(R.string.toast_remind_uploading),
				true, false, new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});

		String url = Constants.HOST_HEAD + Constants.UPLOAD_NEW_DISH;
		MultiPartJSONRequest multiPartRequest = new MultiPartJSONRequest(
				Request.Method.POST, url, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						try {
							if (!respon.getString("code").equals(1)) {
								Toast.makeText(
										SettingDishImgUpload.this,
										getResources()
												.getString(
														R.string.toast_remind_upload_succeed),
										Toast.LENGTH_SHORT).show();
								mProgressHUD.dismiss();
								finish();
							} else {
								Toast.makeText(
										SettingDishImgUpload.this,
										getResources()
												.getString(
														R.string.toast_remind_upload_failed)
												+ respon.getString("msg"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							mProgressHUD.dismiss();
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DebugFlags.logD(TAG,
								"onErrorResponse " + error.getMessage());
						Toast.makeText(
								SettingDishImgUpload.this,
								getResources().getString(
										R.string.toast_remind_upload_failed),
								Toast.LENGTH_SHORT).show();
						mProgressHUD.dismiss();
					}
				}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("Content-Disposition", String.format(
						"form-data; name=\"%s\"; filename=\"%s\"", "file",
						"new_dish_img.png"));

				return map;
			}

			@Override
			public Map<String, Bitmap> getFileUploads() {
				Map<String, Bitmap> fileParams = new HashMap<String, Bitmap>();
				fileParams.put("file", btMap);
				return fileParams;
			}

			@Override
			public Map<String, String> getStringUploads() {
				Map<String, String> strParams = new HashMap<String, String>();
				strParams.put("sellerId", Constants.SELLER_ID);
				strParams.put("classId", String.valueOf(categoryId));
				strParams.put("goodsName", dishNameEdit.getText().toString());
				strParams.put("goodsPrice", dishPriceEdit.getText().toString());
				strParams.put("goodsType",
						String.valueOf(CURRENT_SELECT_DISH_TYPE));
				return strParams;
			}

		};

		mSingleQueue.add(multiPartRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_EX && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			ContentResolver cr = this.getContentResolver();
			try {
				btMap = BitmapFactory.decodeStream(cr
						.openInputStream(selectedImage));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// if (btMap.getHeight() > 384) {
			// // btMap = BitmapFactory.decodeFile(picPath);
			btMap = resizeImage(btMap);
			if (btMap != null) {
				BitmapDrawable bd = new BitmapDrawable(getResources(), btMap);
				addBitMapBtn.setImageDrawable(bd);
			}
			previewBg.setVisibility(View.GONE);
		}
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	public Bitmap resizeImage(Bitmap bitmap) {
		float hscalew = (float) 204 / (float) 318;// 产品图片比例
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		// int newWidth = width;
		int newHeight = (int) (width * hscalew);

		int retY = (height - newHeight) / 2;
		Matrix matrix = new Matrix();
		matrix.postScale(1, 1);
		try {
			Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, retY,
					width, newHeight, matrix, true);
			return resizedBitmap;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Toast.makeText(SettingDishImgUpload.this, "截图失败！",
					Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	@SuppressLint("NewApi")
	void showSelectDialog() {
		Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setTitle("请选择类型");
		// 0: 默认第一个单选按钮被选中
		final String[] typeSet = getResources().getStringArray(
				R.array.dish_category_array);

		builder.setSingleChoiceItems(R.array.dish_category_array,
				CURRENT_SELECT_DISH_TYPE,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CURRENT_SELECT_DISH_TYPE = which;
						if (which == 0) {
							CURRENT_SELECT_DISH_TYPE = DISH_TYPE_MAIN;
						} else if (which == 1) {
							CURRENT_SELECT_DISH_TYPE = DISH_TYPE_APPEND;
						}
						typeSelector.setText(typeSet[which]);
						dialog.dismiss();
					}
				});
		// 创建一个单选按钮对话框
		builder.create().show();
	}
}
