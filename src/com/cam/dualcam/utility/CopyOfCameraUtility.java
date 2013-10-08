package com.cam.dualcam.utility;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CopyOfCameraUtility {
	//TAG
	public static String TAG = "CameraUtility";
	public int result;
	public CameraInfo info;
	public static int cameraId = -1;
	private Context context;
	private Parameters params;
	
	public int cameraHeight = 0;
	public int cameraWidth = 0;
	public int picHeight = 0;
	public int picWidth = 0;
	
	public int chosenWidth = 0;
	public int chosenHeight = 0;
	public int lastChanceWidth = 0;
	public int lastChanceHeight = 0;
//	public int defaultWidth = 640;
	public int defaultWidth = 480;
	public int defaultHeight = 480;
	public boolean hasDefaultSize = false;
	
	public CopyOfCameraUtility(Context localContext){
		context = localContext;
	}

public Camera getCameraInstance(String side, int width, int height){
    Camera c = null;
    try {
        c = Camera.open(findCamera(side)); // attempt to get a Camera instance
        
        params = c.getParameters();
        List<Camera.Size> size = params.getSupportedPreviewSizes();
        List<Camera.Size> picS = params.getSupportedPictureSizes();
        Log.d(TAG, "screenWidth = "+width+": screenHeight = "+height);
        Log.d(TAG, "Log size = "+size.size());
        Log.d(TAG, "Log picS = "+picS.size());
        Camera.Size camsize = size.get(0);
        for(int i=0;i<size.size();i++)
        {
        	Log.i(TAG,"Camera.Size Cam @ "+i+" Width = "+size.get(i).width+" : Height = "+size.get(i).height);
        	//if(size.get(i).width > camsize.width)
        	//	camsize = size.get(i);
        	if(defaultWidth == size.get(i).width && defaultHeight == size.get(i).height){
        		hasDefaultSize = true;
        	}

        	if(width == size.get(i).width)
        	{
        		//Log.i(TAG,"defaultSize = "+hasDefaultSize);
        		chosenWidth = size.get(i).width;
        		chosenHeight = size.get(i).height;
        		break;
        	}
        	
        	
        }
        Camera.Size picsize = picS.get(0);
        for(int i=0;i<picS.size();i++)
        {
        	//Log.i(TAG,"Camera.Size Pic @ "+i+" Width = "+picS.get(i).width+" : Height = "+picS.get(i).height);
        	if(picS.get(i).width > picsize.width)
        		picsize = picS.get(i);
        	
        }
        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        params.setExposureCompensation(0);
        //params.setPreviewSize(camsize.width,camsize.height);
        if(side == "FRONT"){
        	params.setPreviewSize(640,480);
        	params.setPictureSize(640,480);
        	chosenWidth = defaultWidth;
    		chosenHeight = defaultHeight;
        }
        else{
        	Log.i(TAG,"chosenWidth = "+chosenWidth+" : chosenHeight = "+chosenHeight);
        	Log.i(TAG, "screenWidth = "+width+": screenHeight = "+height);
        	if(chosenWidth != 0){
        		params.setPreviewSize(chosenWidth,chosenHeight);
        		params.setPictureSize(chosenWidth,chosenHeight);
        		
        	}
        	else{
        		//params.setPreviewSize(camsize.width,camsize.height);
        		//params.setPictureSize(picS.get(0).width,picS.get(0).height);
        		
        		if(hasDefaultSize){
        			params.setPreviewSize(defaultWidth,defaultHeight);
        			params.setPictureSize(defaultWidth,defaultHeight);
        			chosenWidth = defaultWidth;
            		chosenHeight = defaultHeight;
        		}
        		else{
        			Log.i(TAG, "Has no Default Size");
        			params.setPreviewSize(size.get(size.size() - 1).width,size.get(size.size() - 1).height);
        			params.setPictureSize(picS.get(size.size() - 1).width,picS.get(size.size() - 1).height);
        			chosenWidth = size.get(size.size() - 1).width;
            		chosenHeight = size.get(size.size() - 1).height;
        		}
        		//params.setPreviewSize(720,480);
    			//params.setPictureSize(720,480);
        	}
        		
        }
        
        
        
        
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			// set the focus mode
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
        cameraWidth =  chosenWidth;
        cameraHeight =  chosenHeight;
        picWidth =  params.getPreviewSize().width;
        picHeight =  params.getPreviewSize().height;
        Log.i(TAG, "picWidth = "+picWidth+":  picHeight = "+ picHeight);
        //if(width < cameraWidth){
        //	params.setPictureSize((cameraWidth/width),camsize.width);
        //}
        //params.setPictureSize(camsize.height,camsize.width);
        //params.setPictureSize(height,width);
        //params.
        //params.setJpegQuality(100);
        //params.setRotation(90);
        //cameraWidth = camsize.width;
        //cameraHeight= camsize.height;
        //Log.i(TAG,"sizesW = "+camsize.width+" : sizeH = "+camsize.height);
        c.setParameters(params);
        
        
    }
    catch (Exception e){
        // Camera is not available (in use or does not exist)
    	Log.d(TAG, "Something shit happened: e @"+e.getCause());
    	Toast.makeText(context.getApplicationContext(),"Something is wrong with the camera setting : CAUSE = "+e.getMessage(),Field.SHOWTIME).show();
    	
    }
    //Log.d(TAG, "Something shit happened: c @"+c);
    return c; // returns null if camera is unavailable
}
public int getPicWidth(){
	return picWidth;
}

public int getPicHeight(){
	return picHeight;
}
public int getCamWidth(){
	return cameraWidth;
}

public int getCamHeight(){
	return cameraHeight;
}

/** Check if this device has a camera */
public boolean checkCameraHardware() {
    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
        // this device has a camera
        return true;
    } else {
        // no camera on this device
        return false;
    }
}

public int findFrontFacingCamera() {
    int cameraId = -1;
    // Search for the front facing camera
    int numberOfCameras = Camera.getNumberOfCameras();
    for (int i = 0; i < numberOfCameras; i++) {
      info = new CameraInfo();
      Camera.getCameraInfo(i, info);
      if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
        Log.d(TAG, "Camera found");
        cameraId = i;
        break;
      }
    }
    return cameraId;
  }

public static int findCamera(String side) {
    
    // Search for the front facing camera
    int numberOfCameras = Camera.getNumberOfCameras();
    //int rotation = context.getWindowManager().getDefaultDisplay()
    //        .getRotation();
    for (int i = 0; i < numberOfCameras; i++) {
      CameraInfo info = new CameraInfo();
      Camera.getCameraInfo(i, info);
      
      if(side == "FRONT"){
    	  if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
    		  Log.d(TAG, "Front Camera found: ID @"+i);
    		  cameraId = i;
    		  
    		  break;
    	  }
      }
      /*
      else if(side == "BACK"){
    	  if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
    		  Log.d(TAG, "Back Camera found: ID @"+i);
    		  cameraId = i;
    		  break;
    	  }
      }
      */
      else {
    	  if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
    		  Log.d(TAG, "Back Camera found: ID @"+i);
    		  cameraId = i;
    		  break;
    	  }
      }
      
    }
    return cameraId;
  }


	PictureCallback myPictureCallback_JPG = new PictureCallback(){

	 @Override
	 public void onPictureTaken(byte[] arg0, Camera arg1) {
	  // TODO Auto-generated method stub
	  Bitmap bitmapPicture
	   = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
	 }};


	 
	 
}
