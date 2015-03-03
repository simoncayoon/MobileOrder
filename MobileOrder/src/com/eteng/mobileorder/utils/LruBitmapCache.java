package com.eteng.mobileorder.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.eteng.mobileorder.debug.DebugFlags;

public class LruBitmapCache extends LruCache<String, Bitmap> implements
		ImageCache {
	
	private static final String TAG = LruBitmapCache.class.getSimpleName();

	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		return cacheSize;
	}

	public LruBitmapCache() {
		this(getDefaultLruCacheSize());
	}

	public LruBitmapCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		DebugFlags.logD(TAG, "缓存中的Url " + url);
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}
