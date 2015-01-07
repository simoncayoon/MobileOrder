package com.eteng.mobileorder.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class InstallHelper {
	/*
	 * public void install(Context context,String apkPath,String apkName) { File
	 * file = new File(apkPath); if( !file.exists()) return ; Uri mPackageURI =
	 * Uri.fromFile(file); int installFlags = 0; PackageManager pm =
	 * context.getPackageManager(); PackageInfo info =
	 * pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES); if(info
	 * != null){ try { PackageInfo pi =
	 * pm.getPackageInfo(info.packageName,PackageManager
	 * .GET_UNINSTALLED_PACKAGES); if( pi != null){ installFlags |=
	 * PackageManager.INSTALL_REPLACE_EXISTING; } } catch (NameNotFoundException
	 * e) { } //把包名和apkName对应起来，后面需要使用 map.put(info.packageName, apkName);
	 * IPackageInstallObserver observer = new PackageInstallObserver();
	 * pm.installPackage(mPackageURI, observer, installFlags, info.packageName);
	 * } }
	 */

	/**
	 * 安裝应用
	 * @param context
	 * @param filePath
	 */
	public static void install(Context context, String filePath) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + filePath),
				"application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	public static boolean isAvilible(Context context, String packageName){ 
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager 
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息 
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名 
       //从pinfo中将包名字逐一取出，压入pName list中 
            if(pinfo != null){ 
            for(int i = 0; i < pinfo.size(); i++){ 
                String pn = pinfo.get(i).packageName; 
                pName.add(pn); 
            } 
        } 
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE 
  }
	
	public static String install(String apkAbsolutePath)
    {
        String[] args = {
                "pm", "install", "-f", apkAbsolutePath
        };
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1)
            {
                baos.write(read);
            }
            
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1)
            {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (Exception e)
        {
            // TODO: handle exception
        }

        return result;

    }
	
}
