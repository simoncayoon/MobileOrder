//package com.eteng.mobileorder.utils;
//
//import java.io.ByteArrayOutputStream;
//import java.nio.ByteBuffer;
//
//import android.annotation.TargetApi;
//import android.graphics.Bitmap;
//import android.graphics.Matrix;
//import android.os.Build;
//
//public class BitmapUtil {
//
//	public static Bitmap resizeImage(Bitmap bitmap, int repWith, int reqHeight) {
//		Bitmap BitmapOrg = bitmap;
//		int width = BitmapOrg.getWidth();
//		int height = BitmapOrg.getHeight();
//		int newWidth = repWith;
//		int newHeight = reqHeight;
//
//		float scaleWidth = ((float) newWidth) / width;
//		float scaleHeight = ((float) newHeight) / height;
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleWidth);
//		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
//				height, matrix, true);
//		return resizedBitmap;
//	}
//
//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	public static byte[] convertBitmapToBytes(Bitmap bitmap) {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//			ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
//			bitmap.copyPixelsToBuffer(buffer);
//			return buffer.array();
//		} else {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//			byte[] data = baos.toByteArray();
//			return data;
//		}
//	}
//}
