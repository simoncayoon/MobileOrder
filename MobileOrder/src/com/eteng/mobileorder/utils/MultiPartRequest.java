package com.eteng.mobileorder.utils;

import java.util.Map;

import android.graphics.Bitmap;

public interface MultiPartRequest {

	
	public void addFileUpload(String param,Bitmap file);   
    
    public void addStringUpload(String param,String content);   
      
    public Map<String,Bitmap> getFileUploads();  
      
    public Map<String,String> getStringUploads();   
}
