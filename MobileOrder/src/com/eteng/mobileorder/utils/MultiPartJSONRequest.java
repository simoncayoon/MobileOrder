package com.eteng.mobileorder.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class MultiPartJSONRequest extends Request<JSONObject> implements
		MultiPartRequest {

	private static final String TAG = MultiPartJSONRequest.class
			.getSimpleName();
	protected static final String TYPE_UTF8_CHARSET = "charset=UTF-8";

	private final Listener<JSONObject> mListener;
	/* To hold the parameter name and the File to upload */
	private Map<String, Bitmap> BitmapUploads = new HashMap<String, Bitmap>();

	/* To hold the parameter name and the string content to upload */
	private Map<String, String> stringUploads = new HashMap<String, String>();

	/**
	 * Creates a new request with the given method.
	 * 
	 * @param method
	 *            the request {@link Method} to use
	 * @param url
	 *            URL to fetch the string at
	 * @param listener
	 *            Listener to receive the String response
	 * @param errorListener
	 *            Error listener, or null to ignore errors
	 */
	public MultiPartJSONRequest(int method, String url,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
		setRetryPolicy(new RetryPolicy() {

			@Override
			public void retry(VolleyError arg0) throws VolleyError {

			}

			@Override
			public int getCurrentTimeout() {
				// TODO Auto-generated method stub
				return DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12;
			}

			@Override
			public int getCurrentRetryCount() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
	}

	public void addFileUpload(String param, Bitmap file) {
		BitmapUploads.put(param, file);
	}

	public void addStringUpload(String param, String content) {
		stringUploads.put(param, content);
	}

	/**
	 * 要上传的文件
	 */
	public Map<String, Bitmap> getFileUploads() {
		return BitmapUploads;
	}

	/**
	 * 要上传的参数
	 */
	public Map<String, String> getStringUploads() {
		return stringUploads;
	}

	/**
	 * 空表示不上传
	 */
	public String getBodyContentType() {
		return "multipart/form-data;";
		// return null;
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		if (mListener != null) {
			mListener.onResponse(response);
		}

	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		String type = response.headers.get(HTTP.CONTENT_TYPE);
		if (type == null) {
			response.headers.put(HTTP.CONTENT_TYPE, type);
		} else if (!type.contains("UTF-8")) {
			type += ";" + TYPE_UTF8_CHARSET;
			response.headers.put(HTTP.CONTENT_TYPE, type);
		}
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

}
