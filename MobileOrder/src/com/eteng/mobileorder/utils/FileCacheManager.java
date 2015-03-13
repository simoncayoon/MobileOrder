package com.eteng.mobileorder.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.StatFs;
import android.widget.ImageButton;

import com.eteng.mobileorder.debug.DebugFlags;

public class FileCacheManager {

	private static final String TAG = FileCacheManager.class.getSimpleName();
	private static FileCacheManager instance = null;
	private static String sellerId = null;
	private Context mContext = null;
	private static final String CACHDIR = "/MobileOrder/.ImgCache";

	private static final int MB = 1024 * 1024;
	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

	private FileCacheManager(Context mContext) {
		this.mContext = mContext;
		sellerId = String.valueOf(TempDataManager.getInstance(mContext)
				.getSellerId());
	}

	public static FileCacheManager getInstance(Context mContext) {
		if (instance == null) {
			instance = new FileCacheManager(mContext);
		}
		return instance;
	}

	/** 从缓存中获取图片 **/
	public Bitmap getImage(String imgPath) {
		final String path = getDirectory() + "/"
				+ convertUrlToFileName(imgPath);
		Bitmap bmp = BitmapFactory.decodeFile(path);
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		DebugFlags.logD(TAG, "原始图片的宽：" + w);
		DebugFlags.logD(TAG, "原始图片的高：" + w);
		
		return resizeImage(bmp, 48 * 8, h);
	}

	public void deleteImgById(String imgPath) {
		final String path = getDirectory() + "/"
				+ convertUrlToFileName(imgPath);
		File file = new File(path);
		if (file.delete()) {
			DebugFlags.logD(TAG, "删除成功!");
		} else {
			DebugFlags.logD(TAG, "删除失败!");
		}
	}

	/** 判断是否有文件 **/
	public Boolean isExists(String imgPath) {
		File file = null;
		String finalName = convertUrlToFileName(imgPath);
		if (!finalName.equals("")) {
			final String path = getDirectory() + "/"
					+ convertUrlToFileName(imgPath);
			file = new File(path);
		}
		return file.exists();
	}

	public void saveRemoteImg(final String remotePath) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Bitmap icon = null;
				InputStream in = null;
				BufferedOutputStream out = null;

				String imgPathHead = "http://222.86.191.71:8080/cms";
				try {
					in = new BufferedInputStream(new URL(imgPathHead
							+ remotePath).openStream(), MB);
					final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
					out = new BufferedOutputStream(dataStream, MB);
					copy(in, out);
					out.flush();
					byte[] data = dataStream.toByteArray();
					icon = BitmapFactory.decodeByteArray(data, 0, data.length);
					// 保存获取到的图片保存到本地SD卡
					saveBitmap(icon, remotePath);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
	}

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[MB];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	/** 将图片存入文件缓存 **/
	public void saveBitmap(Bitmap bm, String imgPath) {
		if (bm == null) {
			return;
		}
		// 判断sdcard上的空间
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			// SD空间不足
			return;
		}
		if (isExists(imgPath)) {
			return;
		}
		String filename = convertUrlToFileName(imgPath);
		String dir = getDirectory();

		File dirFile = new File(dir);
		if (!dirFile.exists())
			dirFile.mkdirs();

		File file = new File(dir + "/" + filename);
		try {
			if (file.createNewFile()) {
				OutputStream outStream = new FileOutputStream(file);
				bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				outStream.flush();
				outStream.close();
			} else {
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 修改文件的最后修改时间 **/
	public void updateFileTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/** 计算sdcard上的剩余空间 **/
	private int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	/**
	 * 将url转成文件名
	 * 
	 * @throws NoSuchAlgorithmException
	 **/
	private String convertUrlToFileName(String imgPath) {
		// MD5加密
		String mergeUrl = sellerId + imgPath;
		MD5 md5 = new MD5();
		String fileName = "";
		try {
			fileName = md5.getMD5(mergeUrl);
			DebugFlags.logD(TAG, "new FIleName is " + fileName);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	/** 获得缓存目录 **/
	private String getDirectory() {
		String dir = getPath() + CACHDIR;
		return dir;
	}

	/** 取SD卡路径 **/
	private String getPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}

	public class MD5 {

		public String getMD5(String val) throws NoSuchAlgorithmException {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());
			byte[] m = md5.digest();// 加密
			return getString(m);
		}

		private String getString(byte[] b) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				sb.append(b[i]);
			}
			return sb.toString();
		}
	}

	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}
}
