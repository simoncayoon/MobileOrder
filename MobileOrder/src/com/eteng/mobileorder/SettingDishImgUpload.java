package com.eteng.mobileorder;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.eteng.mobileorder.cusomview.TopNavigationBar;
import com.eteng.mobileorder.cusomview.TopNavigationBar.NaviBtnListener;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.utils.DisplayMetrics;
import com.eteng.mobileorder.utils.MultiPartStack;
import com.eteng.mobileorder.utils.MultiPartStringRequest;

public class SettingDishImgUpload extends Activity implements NaviBtnListener,
		OnClickListener {

	private static final String TAG = "SettingDishImgUpload";
	private static final String filePrefix = "com.eteng.mobileorder";
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
	private String bmpName = "";
	private int CURRENT_SELECT_DISH_TYPE = -1;// 当前选中的菜品类型
	RequestQueue mSingleQueue;

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
			if(btMap == null){
				Toast.makeText(SettingDishImgUpload.this, "请选择菜品图片", Toast.LENGTH_SHORT).show();
				return;
			}
			if(dishNameEdit.getText().toString().length() == 0){
				Toast.makeText(SettingDishImgUpload.this, "请输入菜名", Toast.LENGTH_SHORT).show();
				return;
			}
			if(dishPriceEdit.getText().toString().length() == 0){
				Toast.makeText(SettingDishImgUpload.this, "请输入价格", Toast.LENGTH_SHORT).show();
				return;
			}
			if(CURRENT_SELECT_DISH_TYPE == -1){
				Toast.makeText(SettingDishImgUpload.this, "请选择类型", Toast.LENGTH_SHORT).show();
				return;
			}
			
			postImgAction();
		}
	}
	
	private void postImgAction(){
		
		String url = Constants.HOST_HEAD + Constants.UPLOAD_NEW_DISH;
		MultiPartStringRequest multiPartRequest = new MultiPartStringRequest(
	            Request.Method.POST, url, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						DebugFlags.logD(TAG, "提交结果 " + respon);
						
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DebugFlags.logD(TAG, "onErrorResponse " + error.getMessage());
					}
				}) {

	        @Override
	        public Map<String, Bitmap> getFileUploads() {
	        	Map<String, Bitmap> fileParams = new HashMap<String, Bitmap>(); 
	        	fileParams.put("testImage", btMap);
	            return fileParams;
	        }

	        @Override
	        public Map<String, String> getStringUploads() {
	        	Map<String, String> strParams = new HashMap<String, String>();
	        	strParams.put("sellerId", Constants.SELLER_ID);
	        	strParams.put("classId", String.valueOf(categoryId));
	        	strParams.put("goodsName", dishNameEdit.getText().toString());
	        	strParams.put("goodsPrice", dishPriceEdit.getText().toString());
	        	strParams.put("goodsType", String.valueOf(CURRENT_SELECT_DISH_TYPE));
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
			bmpName = data.getDataString();
			if(bmpName.length() == 0){
				Toast.makeText(SettingDishImgUpload.this, "该图片没有名称！", Toast.LENGTH_SHORT).show();
				return;
			}
			ContentResolver cr = this.getContentResolver();

			try {
				btMap = BitmapFactory.decodeStream(cr
						.openInputStream(selectedImage));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (btMap.getHeight() > 384) {
				// btMap = BitmapFactory.decodeFile(picPath);
				btMap = resizeImage(btMap, DisplayMetrics.dip2px(this, 200),
						DisplayMetrics.dip2px(this, 100));
				BitmapDrawable bd = new BitmapDrawable(getResources(), btMap);
				addBitMapBtn.setImageDrawable(bd);

			} else {
				BitmapDrawable bd = new BitmapDrawable(getResources(), btMap);
				addBitMapBtn.setImageDrawable(bd);
			}
			previewBg.setVisibility(View.GONE);
		}
	}

	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
//		int newHeight = h;

		float scaleWidth = ((float) newWidth) / width;
//		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	@SuppressLint("NewApi")
	void showSelectDialog() {
		Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setTitle("请选择类型");
		// 0: 默认第一个单选按钮被选中
		final String[] typeSet = getResources().getStringArray(
				R.array.dish_category_array);
		
		builder.setSingleChoiceItems(R.array.dish_category_array, CURRENT_SELECT_DISH_TYPE,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CURRENT_SELECT_DISH_TYPE = which;
						typeSelector.setText(typeSet[which]);
						dialog.dismiss();
					}
				});
		// 创建一个单选按钮对话框
		builder.create().show();
	}
}
