package com.cam.dualcam.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class HideAct implements AnimationListener{
	
	Context context;
	Animation anim;
	//AnimParams animParams = new AnimParams();
	View view;
	boolean isShown;
	int left;
	int top;
	
	public static String TAG 			= "HideAct";
	
	public HideAct(Context appContext,View appView) {
		context = appContext;
		view = appView;
		left = (int) (view.getMeasuredWidth() * 0.8);
		top = (int) (400);
		Log.i(TAG, "Top = "+top +" : Left = "+left);
	}
	
	public void showView(){
		//anim = new TranslateAnimation(0, left, 0, 0);
		anim = new TranslateAnimation(0, 0, -top, 0);
		view.setVisibility(View.VISIBLE);
		anim.setDuration(500);
		//anim.setAnimationListener(context);
		anim.setFillAfter(true);
	}
	
	public void hideView(){
		//anim = new TranslateAnimation(0, -left, 0, 0);
		anim = new TranslateAnimation(0, 0, 0,-top);
		view.setVisibility(View.GONE);
		anim.setDuration(500);
		anim.setFillAfter(true);
	}
	
	public void ninjaMoves(){
		if(view.isShown())
			hideView();
		else if(!view.isShown())
			showView();
		
		view.startAnimation(anim);
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

}
