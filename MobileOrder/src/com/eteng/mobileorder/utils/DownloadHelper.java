package com.eteng.mobileorder.utils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadHelper {

	/**
	 * 应用下载
	 * 
	 * @param context
	 * @param fileUrl
	 * @param mDialog
	 * @param dialogBar
	 */
	public static void downloadApp(final Context context, String fileUrl,
			final Dialog mDialog, final ProgressBar dialogBar,
			final TextView rateTextView) {
		Log.d("DownloadHelper", "fileUrl:" + fileUrl);
		FinalHttp fh = new FinalHttp();
		// 调用download方法开始下载
		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpHandler handler = fh.download(fileUrl, // 这里是下载的路径
				"/mnt/sdcard/mobile_order.apk", // 这是保存到本地的路径
				// true,// true:断点续传 false:不断点续传（全新下载）
				new AjaxCallBack() {

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						mDialog.dismiss();
						Toast.makeText(context, strMsg, Toast.LENGTH_LONG)
								.show();
						super.onFailure(t, errorNo, strMsg);
					}

					@Override
					public void onLoading(long count, long current) {

						dialogBar.setMax((int) count);
						dialogBar.setProgress((int) current);
						dialogBar.postInvalidate();
						rateTextView.setText(CommonUtil.getPercent(current,
								count));

						super.onLoading(count, current);
					}

					@Override
					public void onSuccess(Object t) {
						mDialog.dismiss();

						// 安装应用
						InstallHelper.install(context,
								"/mnt/sdcard/mobile_order.apk");

						super.onSuccess(t);
					}

					@Override
					public void onStart() {

						super.onStart();
					}

					@Override
					public AjaxCallBack progress(boolean progress, int rate) {

						return super.progress(progress, rate);
					}

				});
	}

}
