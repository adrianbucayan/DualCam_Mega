package com.cam.dualcam.utility;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class PhoneChecker {
	
	private Context context;
	
	public Integer screenWidth;
	public Integer screenHeight;
	
	public PhoneChecker(Context localContext){
		context = localContext;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		Display display = wm.getDefaultDisplay();
//		screenWidth = display.getWidth();
//		screenHeight = display.getHeight();
//		Display display2 = getWindowManager().getDefaultDisplay(); 
		//Display display = getWindowManager().getDefaultDisplay();
	}
	
	
	

}
