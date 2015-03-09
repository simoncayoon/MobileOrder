package com.eteng.mobileorder.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eteng.mobileorder.R;
import com.eteng.mobileorder.cusomview.ProgressHUD;
import com.eteng.mobileorder.debug.DebugFlags;
import com.eteng.mobileorder.models.CategoryInfo;
import com.eteng.mobileorder.models.Constants;
import com.eteng.mobileorder.models.DishInfo;
import com.eteng.mobileorder.models.RemarkInfo;

public class GetRemoteDateHelper {

	public static final String TAG = GetRemoteDateHelper.class.getSimpleName();

	private Context mContext = null;
	private Context appContext = null;
	ProgressHUD mProgressHUD;
	private static int loadCount = 0;
	private static int maxCount = 0;

	private ShowData mDummyCall = new ShowData() {

		@Override
		public void showData() {
			// TODO Auto-generated method stub

		}
	};

	private ShowData callBack = mDummyCall;

	public GetRemoteDateHelper(Context context, Context appContext) {
		mContext = context;
		this.appContext = appContext;
	}

	public void getRemoteDate() {
		if (mContext instanceof ShowData) {
			callBack = (ShowData) mContext;
		} else {
			return;
		}
		DbHelper.getInstance(mContext).clearAllDataAboutDish();
		loadCount = 0;
		maxCount = 0;
		getMenuCategory();
	}

	/**
	 * 获取菜单类型
	 */
	public void getMenuCategory() {
		if (((ArrayList<CategoryInfo>) DbHelper.getInstance(mContext)
				.getLocalCategory()).size() > 0) {// 存在本地数据
			DebugFlags.logD(TAG, "存在数据！！！");
		} else {
			DebugFlags.logD(TAG, "不存在，在线加载");
			mProgressHUD = ProgressHUD.show(mContext, mContext.getResources()
					.getString(R.string.toast_remind_loading), true, false,
					new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {

						}
					});
			String url = Constants.HOST_HEAD + Constants.MENU_BY_ID;
			Uri.Builder builder = Uri.parse(url).buildUpon();
			builder.appendQueryParameter(
					"sellerId",
					mContext.getSharedPreferences(
							Constants.SP_GENERAL_PROFILE_NAME,
							Context.MODE_PRIVATE).getString(
							Constants.SP_SELLER_ID, ""));
			JsonUTF8Request getMenuRequest = new JsonUTF8Request(
					Request.Method.GET, builder.toString(), null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject respon) {
							loadCount++;
							insertMenuList(respon);
							dismissHud();
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							loadCount++;
							DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
							dismissHud();
						}
					});
			NetController.getInstance(appContext).addToRequestQueue(
					getMenuRequest, TAG);
		}

	}

	public void insertMenuList(JSONObject JsonString) {
		try {
			JSONArray jsonArray = new JSONArray(
					JsonString.getString("classList"));
			maxCount = jsonArray.length() * 2;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject tmp = new JSONObject(jsonArray.getString(i));
				// +++++++++++++++++++DB+++++++++
				CategoryInfo testItem = new CategoryInfo();
				testItem.setCategoryId(tmp.getLong("classId"));
				testItem.setCategoryCode(tmp.getString("classCode"));
				testItem.setCategoryName(tmp.getString("className"));
				testItem.setCategoryOrder(tmp.getInt("classOrder"));
				testItem.setCategoryStatus(tmp.getString("classStatus"));
				testItem.setCreateDate(tmp.getString("createDate"));
				testItem.setCreatePerson(tmp.getString("createPerson"));
				if (tmp.getString("isNoodle").equals("0")) {
					testItem.setIsNoodle(false);
				} else {
					testItem.setIsNoodle(true);
				}
				testItem.setSellerId(tmp.getLong("sellerId"));
				DbHelper.getInstance(mContext).insertCategory(testItem);
				// +++++++++++++++++++DB++++++++
				insertDishInfo(testItem.getCategoryId());
				getOptions(testItem.getCategoryId());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 获取相应种类下的所有数据
	 */
	public void insertDishInfo(Long categoryId) {
		String url = Constants.HOST_HEAD + Constants.GOODS_BY_ID;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter(
				"sellerId",
				mContext.getSharedPreferences(
						Constants.SP_GENERAL_PROFILE_NAME, Context.MODE_PRIVATE)
						.getString(Constants.SP_SELLER_ID, ""));
		builder.appendQueryParameter("goodsClass", String.valueOf(categoryId));
		builder.appendQueryParameter("page", Constants.PAGE);
		builder.appendQueryParameter("pageCount", Constants.PAGE_COUNT);
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						loadCount++;
						try {
							if (respon.getString("code").equals("0")) {// 查询成功

								String jsonString = respon
										.getString("goodsList");
								parseJson(jsonString);
							} else {
								try {
									throw new VolleyError();
								} catch (VolleyError e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissHud();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						loadCount++;
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
						dismissHud();
					}
				});
		NetController.getInstance(appContext).addToRequestQueue(getMenuRequest,
				TAG);

	}

	/**
	 * 将JSON字符串数据解析到链表
	 * 
	 * @param jsonString
	 * @throws JSONException
	 */
	protected void parseJson(String jsonString) throws JSONException {

		JSONArray srcList = new JSONArray(jsonString);
		for (int i = 0; i < srcList.length(); i++) {
			JSONObject temp = new JSONObject(srcList.getString(i));
			// **************DB****************//
			DishInfo testInfo = new DishInfo();
			testInfo.setDishId(temp.getLong("goodsId"));
			testInfo.setDishName(temp.getString("goodsName"));

			testInfo.setDishImgPath(temp.getString("goodsImgPath"));
			testInfo.setCreatePerson(temp.getString("createPerson"));
			testInfo.setCreateDate(temp.getString("createTime"));

			testInfo.setDishSummary(temp.getString("goodProduction"));
			testInfo.setDishStock(temp.getString("goodsNumber"));
			try {
				testInfo.setDishOrder(Integer.valueOf(temp
						.getString("goodsOrder")));
				testInfo.setPrice(Float.valueOf(temp.getString("goodsPrice")));
				testInfo.setDishCategory(Long.valueOf(temp.getInt("goodsClass")));
				testInfo.setDiscountPrice(Float.valueOf(temp
						.getString("discountPrice")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				continue;
			}

			testInfo.setDishSerial(temp.getString("goodsSerial"));
			testInfo.setDishStatus(temp.getString("goodsStatus"));
			testInfo.setDishType(temp.getString("goodsType"));
			DbHelper.getInstance(mContext).saveDish(testInfo);
			// **************DB****************//
		}
	}

	/***
	 * 获取相应种类下的备注数据
	 */
	private void getOptions(Long categoryId) {

		String url = Constants.HOST_HEAD + Constants.OPTION_REMARK;
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter(
				"sellerId",
				mContext.getSharedPreferences(
						Constants.SP_GENERAL_PROFILE_NAME, Context.MODE_PRIVATE)
						.getString(Constants.SP_SELLER_ID, ""));
		builder.appendQueryParameter("classId", String.valueOf(categoryId));
		JsonUTF8Request getMenuRequest = new JsonUTF8Request(
				Request.Method.GET, builder.toString(), null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject respon) {
						loadCount++;
						try {
							if (respon.getString("code").equals("0")) {// 查询成功
								parseRemark(new JSONArray(
										respon.getString("optionList")));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dismissHud();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						loadCount++;
						dismissHud();
						DebugFlags.logD(TAG, "oops!!! " + arg0.getMessage());
					}
				});
		NetController.getInstance(appContext).addToRequestQueue(getMenuRequest,
				TAG);
	}

	void parseRemark(JSONArray options) throws JSONException {
		for (int i = 0; i < options.length(); i++) {
			JSONObject temp = new JSONObject(options.getString(i).toString());
			RemarkInfo item = new RemarkInfo();
			item.setId(temp.getLong("id"));
			item.setSellerId(temp.getLong("sellerId"));
			item.setRemarkName(temp.getString("optionName"));
			item.setRemarkStatus(temp.getString("optionStatus"));
			item.setOrder(temp.getInt("optionOrder"));
			item.setBelongsToId(temp.getLong("classId"));
			item.setSelectStat(false);
			DbHelper.getInstance(mContext).saveRemark(item);
		}
	}

	void dismissHud() {
		DebugFlags.logD(TAG, "loadCount " + loadCount);
		if ((loadCount - 1) == maxCount) {
			mProgressHUD.dismiss();
			callBack.showData();
			callBack = mDummyCall;
		}
	}

	public interface ShowData {
		public void showData();
	}
}
