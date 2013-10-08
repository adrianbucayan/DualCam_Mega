package com.cam.dualcam.utility;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackageCheck {
	
	private Context context;
	
	public PackageCheck(Context localContext){
		context = localContext;
	}
	
	public boolean isPackageExists(String target) {
    	boolean package_exists = false;
    	try {
    	    ApplicationInfo info = context.getPackageManager().getApplicationInfo(target, 0);
    	    package_exists = true;
    	    System.out.println("from main menu: package exists...");
    	    System.out.println("from main menu: package info: " + info);
    	} catch(PackageManager.NameNotFoundException e) {
    		package_exists = false;
    		System.out.println("from main menu: package does not exists...");
    	}
    	return package_exists;
    }
	

}

