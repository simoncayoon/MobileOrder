package com.eteng.mobileorder.utils;

import java.io.UnsupportedEncodingException;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

public class JsonUTF8Request extends JsonRequest<JSONObject> {

	protected static final String TAG = "JsonUTF8Request";
	protected static final String TYPE_UTF8_CHARSET = "charset=UTF-8";

	public JsonUTF8Request(int method, String url, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest
				.toString(), listener, errorListener);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

		String type = response.headers.get(HTTP.CONTENT_TYPE);
		if (type == null) {
			type = TYPE_UTF8_CHARSET;
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
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

}
