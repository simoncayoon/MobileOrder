package com.eteng.mobileorder.utils;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class CommonUtil {
	  public static String getPercent(long y, long z) {
	        String baifenbi = "";// 接受百分比的值
	        double baiy = y * 1.0;
	        double baiz = z * 1.0;
	        double fen = baiy / baiz;
	        // NumberFormat nf = NumberFormat.getPercentInstance(); 注释掉的也是一种方法
	        // nf.setMinimumFractionDigits( 2 ); 保留到小数点后几位
	        DecimalFormat df1 = new DecimalFormat("##.00%"); // ##.00%
	                                                            // 百分比格式，后面不足2位的用0补齐
	        // baifenbi=nf.format(fen);
	        baifenbi = df1.format(fen);
	        System.out.println(baifenbi);
	        return baifenbi;
	    }
	  /**
	   * 获取版本号
	   * @return 当前应用的版本号
	   */
	  public static int getVersion(Context context) {
		  int result=1;
	      try {
	          PackageManager manager = context.getPackageManager();
	          PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
	          result = info.versionCode;
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	      
	      return result;
	  }
}
