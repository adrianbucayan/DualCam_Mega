//package com.cam.dualcam;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Point;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.hardware.Camera;
//import android.hardware.Camera.AutoFocusCallback;
//import android.hardware.Camera.CameraInfo;
//import android.hardware.Camera.ErrorCallback;
//import android.hardware.Camera.Parameters;
//import android.hardware.Camera.PictureCallback;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Display;
//import android.view.Surface;
//import android.view.View;
//import android.view.Menu;
//import android.view.View.OnClickListener;
//import android.view.View.OnLongClickListener;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.view.WindowManager;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.Button;
//import android.widget.ImageView.ScaleType;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import android.view.OrientationEventListener;
//
//
//import com.cam.dualcam.utility.*;
//import com.cam.dualcam.bitmap.*;
//import com.cam.dualcam.view.CameraPreview;
//
//@SuppressLint("NewApi")
//public class CopyOfDualCamActivity extends Activity implements OnClickListener {
//	
//	//Defined variables
//	public static String TAG = "DualCamActivity";
//
//	Bitmap bitmap, picTaken = null;
//	
// 	public ImageView cumShotPreviewTop,cumShotPreviewBottom,previewImage;
// 	public Button button;
// 	
// 	//Utility
// 	public ImageView smileyButton, saveButton, retryButton, shareButton, retakeBackButton, retakeFrontButton;
// 	public AlertDialog myAlert;
// 	
// 	public Uri photoUri;
//	public Integer resultSet = 0;
//	
//	public Intent sharingIntent;
//	public PackageCheck packageCheck;
//	public MediaUtility mediaUtility;
//	public static CameraUtility cameraUtility;
//	public BitmapResizer bitmapResizer;
//	
//	public Parameters param;
//	public Camera mCamera;
//    public CameraPreview mPreview;
//	public FrameLayout preview, previewBack, previewFront;
//	public RelativeLayout previewLayout;
//	public LinearLayout pictureLayout,topL,bottomL;
//	public ViewGroup prevGroup;
//	
//	public String phoneModel = android.os.Build.MODEL;
//	public String side, fileName;
//	
//	public static int result = 0;
//	public static int degrees = 0;
//	public Integer definedWidth = 480;
//	public Integer definedHeight = 640;
//	public Integer shortHeight;
//	public Integer shortWidth;
//	public Integer screenHeight;
//	public Integer screenWidth;
//	public BitmapFactory.Options options;
//	
//	public boolean saveable = false;
//	public boolean saved = false;
//	public boolean retakeBack = false;
//	public boolean retakeFront = false;
//	public boolean backPic = false;
//	public boolean frontPic = false;
//	
//	private static SensorManager sensorService;
//	private Sensor sensor;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.dualcam);
//		
////		sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
////	    sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
////	    if (sensor != null) {
////	      sensorService.registerListener(mySensorEventListener, sensor,
////	          SensorManager.SENSOR_DELAY_NORMAL);
////	    } 
//		try{
//			screenHeight = new PhoneChecker(this).screenHeight;
//			screenWidth = new PhoneChecker(this).screenWidth;
//			shortHeight = (int)(screenHeight * 0.4);
//			shortWidth = (int)(screenWidth * 0.4);
//			
//		   
//		   OrientationEventListener myOrientationEventListener
//		   = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL){
//
//		    @Override
//		    public void onOrientationChanged(int control) {
//		     // TODO Auto-generated method stub
//		     //textviewOrientation.setText("Orientation: " + String.valueOf(arg0));
//		    	//Log.i(TAG,"onOrientationChangded: changed = "+control);
//		    }};
//		    if (myOrientationEventListener.canDetectOrientation()){
//		        //Toast.makeText(this, "Can DetectOrientation", Toast.LENGTH_LONG).show();
//		        
//		        myOrientationEventListener.enable();
//		       }
//			
//		}catch(Exception e){
//			
//		}
//		
//		bitmapResizer= new BitmapResizer(getApplicationContext());
//		mediaUtility = new MediaUtility(getApplicationContext());
//		packageCheck = new PackageCheck(getApplicationContext());
//		//imageView = (ImageView) findViewById(R.id.cumshot);
//		//button = (Button) findViewById(R.id.cumbutton);
//		//button.setOnClickListener(this);//takePicture(ShutterCallback, PictureCallback, PictureCallback, PictureCallback)
//		
//		smileyButton = (ImageView) findViewById(R.id.smileyButton);
//		saveButton   = (ImageView) findViewById(R.id.saveButton);
//		retryButton  = (ImageView) findViewById(R.id.retryButton);
//		shareButton  = (ImageView) findViewById(R.id.shareButton);
//		retakeBackButton = (ImageView) findViewById(R.id.retakeback);
//		retakeFrontButton = (ImageView) findViewById(R.id.retakefront);
//		cumShotPreviewTop = (ImageView) findViewById(R.id.cumPreviewBack);
//		cumShotPreviewBottom = (ImageView) findViewById(R.id.cumPreviewFront);
//		previewImage = (ImageView) findViewById(R.id.previewImage);
//		
//		preview = (FrameLayout) findViewById(R.id.cumshot);
//		prevGroup = (ViewGroup) findViewById(R.id.addCamPreview);
//		previewLayout = (RelativeLayout) findViewById(R.id.addCamPreview);
//		pictureLayout = (LinearLayout) findViewById(R.id.picLayout);
//		topL = (LinearLayout) findViewById(R.id.top);
//		bottomL = (LinearLayout) findViewById(R.id.bottom);
//		
//		//previewLayout = (RelativeLayout) findViewById(R.id.addCamPreview);
//		//pictureLayout= (RelativeLayout) findViewById(R.id.picPreview);
//		
//		//cumShotPreviewBottom.setOnClickListener(this);
//		smileyButton.setOnClickListener(this);
//		saveButton.setOnClickListener(this);
//		retryButton.setOnClickListener(this);
//		shareButton.setOnClickListener(this);
//		retakeBackButton.setOnClickListener(this);
//		retakeFrontButton.setOnClickListener(this);
//		cumShotPreviewTop.setOnClickListener(this);
//		cumShotPreviewBottom.setOnClickListener(this);
//		//preview.setOnClickListener(this);
//		
//		
//		cumShotPreviewTop.setOnLongClickListener(new OnLongClickListener(){
//			@Override
//			public boolean onLongClick(View arg0) {
//				mCamera.autoFocus(new AutoFocusCallback(){
//					@Override
//					public void onAutoFocus(boolean arg0, Camera arg1) {
//						//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//						takeAShot();
//					}
//				});
//				
//				return true;
//			}
//		});
//		
//		cumShotPreviewBottom.setOnLongClickListener(new OnLongClickListener(){
//			@Override
//			public boolean onLongClick(View arg0) {
//				mCamera.autoFocus(new AutoFocusCallback(){
//					@Override
//					public void onAutoFocus(boolean arg0, Camera arg1) {
//						//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//						takeAShot();
//					}
//				});
//				return true;
//			}
//		});
//		//cumShotPreviewBottom.setVisibility(ImageView.GONE);
//		//Default, back camera is initiated
//		
//		if (savedInstanceState != null) {
//			picTaken = savedInstanceState.getParcelable("bitmap");
//			settoBackground(getPressedPreview("BACK"), picTaken);
//		}
//		else
//			setSide("BACK");
//	}
//	
//	 @Override
//	  public void onSaveInstanceState(Bundle toSave) {
//	    super.onSaveInstanceState(toSave);
//	    toSave.putParcelable("bitmap", picTaken);
//	  }
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.dual_cam, menu);
//		return true;
//	}
//	
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		
//	}
//	
//	public void onClick(View view) {
//		
//		try{
//			if(view.getId() == R.id.smileyButton){
//				takeAShot();
//				
//			}
//			
//			else if(view.getId() == R.id.saveButton){
//				
//				//if(saveable)
//					try{
//						pictureLayout.buildDrawingCache();
//						saveImage(pictureLayout.getDrawingCache());
//						pictureLayout.destroyDrawingCache();
//						//saveImage();
//					}catch(Exception e){
//						Toast.makeText(getApplicationContext(),"申し訳ありませんが、何かがカメラで間違っていた。",Field.SHOWTIME).show();
//					}
//				//else
//					//Toast.makeText(getApplicationContext(),"You don't want a pic of yourself?",Field.SHOWTIME).show();
//				
//			}
//			
//			else if(view.getId() == R.id.retryButton){
//				linkSTART();
//			}
//			
//			else if(view.getId() == R.id.shareButton){
//				try{
//					Log.i(TAG, "saved = "+saved);
//					if(saved)
//						shareFunction();
//					
//				}catch(Exception e){
//					Log.i(TAG, "saved = "+saved);
//					Log.i(TAG,"ERROR = "+e.getCause());
//				}
//				//else
//					//Toast.makeText(getApplicationContext(),"画像を保存してください",Field.SHOWTIME).show();
//				
//			}
//			
//			else if(view.getId() == R.id.retakeback){
//				if(saveable){
//					setSide("BACK");
//				}
//			}
//			
//			else if(view.getId() == R.id.retakefront){
//				if(saveable){
//					setSide("FRONT");
//				}
//			}
//			
//			else if(view.getId() == R.id.cumPreviewBack){
//				
//				if(saveable){
//					//setSide("BACK");
//					retakeImage("BACK");
//					//createAlert("","","");
//					
//				}
//				else{
//					if(side == "BACK")
//						takeAShot();
//				}
////				else{
////					mCamera.autoFocus(new AutoFocusCallback(){
////						@Override
////						public void onAutoFocus(boolean arg0, Camera arg1) {
////							//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
////						}
////					});
////				}
//			}
//			
//			else if(view.getId() == R.id.cumPreviewFront){
//				if(saveable){
//					//setSide("FRONT");
//					retakeImage("FRONT");
//				}
//				else{
//					if(side == "FRONT")
//						takeAShot();
//				}
////				else{
////					mCamera.autoFocus(new AutoFocusCallback(){
////						@Override
////						public void onAutoFocus(boolean arg0, Camera arg1) {
////							//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
////						}
////					});
////				}
////				
////				if(side == "BACK")
////				{
////					try{
////						//mCamera.takePicture(null, null, s3FixIloveS3);
////						mCamera.setErrorCallback(ec);
////						mCamera.takePicture(null, null, mPicture);
////						//Toast.makeText(getApplicationContext(),"Nice shot!",Field.SHOWTIME).show();
////						
////					}catch(Exception e){
////						//mCamera.takePicture(null, null, s3FixIloveS3);
////						Toast.makeText(getApplicationContext(),"申し訳ありませんが、何かがカメラで間違っていた。",Field.SHOWTIME).show();
////						
////					}
////				}
//			}
//			
//			else if(view.getId() == R.id.cumshot){
////				if(saveable){
////					if(side == "BACK")
////						setSide("BACK");
////					else if(side == "FRONT")
////						setSide("FRONT");	
////				}
//				
//			}
//		}
//		catch(Exception e)
//		{
//			Log.i(TAG,"Error in here View = "+view.getId()+": Cause? I don't effing know -> "+e.getMessage());
//			Toast.makeText(this,"申し訳ありませんが、何かがカメラで間違っていた。",Field.SHOWTIME).show();
//		}
//	}
//	
//	public ErrorCallback ec = new ErrorCallback(){
//
//		@Override
//		public void onError(int data, Camera camera) {
//			Log.i(TAG,"ErrorCallback received");
//			//Toast.makeText(getApplicationContext(),"Sorry, something went wrong with the camera. Error",Field.SHOWTIME).show();
//			//Toast.makeText(getApplicationContext(),"You could save and ",Field.SHOWTIME).show();
//			//saveImage();
//		}
//		
//	};
//
//	public void SShot(){
//		//Bitmap bitmap;
//    	int width = 0;
//    	int height = 0;
//    	Matrix matrix = new Matrix(); 
//    	options = new BitmapFactory.Options();
//  		options.inSampleSize = 1;
//  		//options.inJustDecodeBounds = true;
//  		//Bitmap temp = BitmapFactory.decodeByteArray(data, 0,  data.length);
//		//int xW = temp.getWidth();
//		//int xH = temp.getHeight();
//  		// Determine how much to scale down the image
//  	    //int scaleFactor = Math.min(xW/screenHeight, xH/screenWidth);
//  		// Calculate inSampleSize
//	    //options.inSampleSize = bitmapResizer.calculateInSampleSize(options, shortWidth, shortHeight);
//	    //options.inSampleSize = scaleFactor;
//	    // Decode bitmap with inSampleSize set
//	    //options.inJustDecodeBounds = false;
//    	
//        //final Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
//        //bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//  		mCamera.stopPreview();
//  		mPreview.setDrawingCacheEnabled(true);
//  		mPreview.buildDrawingCache();
//    	bitmap = mPreview.getDrawingCache();
//    	//preview.destroyDrawingCache();
//        boolean b = bitmap == null;
//        Log.i(TAG, "Bitmap == null: = "+b+" Width = "+bitmap.getWidth());
//        //matrix.postRotate(result); 
//        //bitmap.recycle();
//    	Log.i(TAG,"Before scaling");
//        
//        previewImage.setVisibility(ImageView.VISIBLE);
//        BitmapDrawable bd = new BitmapDrawable(bitmap);
//        //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(bitmap.getWidth() /2)+1, bitmap.getHeight());
//        //buttonView.setImageBitmap(bmp);
//        //buttonView.setBackground(bd);
//        
//        
//        int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        	previewImage.setBackgroundDrawable(bd);
//        } else {
//        	previewImage.setBackground(bd);
//        }
//        
//        Log.i(TAG,"Mega Width = "+previewImage.getWidth() );
//        //preview.destroyDrawingCache();
//    	//preview.setDrawingCacheEnabled(false);
//        //preview.setVisibility(FrameLayout.GONE);
//        //previewImage.setImageBitmap(bitmap)
//		//mCamera.stopPreview();
//		
//	}
//	
//	
//
//	public PictureCallback getPic = new PictureCallback() {
//		
//	    @Override
//	    public void onPictureTaken(byte[] data, Camera camera) {
//	    	
//	    	try {
//	    		ImageView buttonView = getPressedPreview(side);
//	    		Matrix matrix = new Matrix(); 
//	    		int width = 0;
//		    	int height = 0;
//		    	int extraWidth = 0 ;
//	           	int extraHeight = 0;
//	           	int marginalWidth = 0;
//	           	int marginalHeight = 0;
//	    		
////	         	if(picTaken != null){
////	           		picTaken.recycle();
////	           		picTaken = null;
////	           	}
//	    		
//		    	
//		    	
//		    	
//		    	Log.i(TAG,"Pic taken");
//		            if(side == "BACK")
//		            {
//		            	Log.i(TAG,"Side = "+side);
//		            	//if(saveable)
//		            	setRetake(side);
//		            	matrix.postRotate(result); 
//		            	
//		            	
//		            	
//		            	//Bitmap bitmap = null;
//				    	//Toast.makeText(getApplicationContext(),"1",1000).show();
//			    		options = new BitmapFactory.Options();
//			 	  		options.inSampleSize = 1;
//			 	  		options.inJustDecodeBounds = true;
//			 	  		
//			 	  		// Determine how much to scale down the image
//			 	  	    //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//			 	  	    
//			 	  		Bitmap temp = BitmapFactory.decodeByteArray(data, 0,  data.length);
//			 			int xW = temp.getWidth();
//			 			int xH = temp.getHeight();
//			 	  		// Determine how much to scale down the image
//			 	  	    //int scaleFactor = Math.min(xW/screenHeight, xH/screenWidth);
//			 			int scaleFactor = Math.max(xW/screenHeight, xH/screenWidth);
//			 	  		// Calculate inSampleSize
//			 		    //options.inSampleSize = bitmapResizer.calculateInSampleSize(options, shortWidth, shortHeight);
//			 		    options.inSampleSize = scaleFactor;
//			 		    // Decode bitmap with inSampleSize set
//			 		    options.inJustDecodeBounds = false;
//			 		    picTaken = BitmapFactory.decodeByteArray(data, 0,  data.length,options);
//			 		    width = picTaken.getWidth();
//			           	height = picTaken.getHeight();
//			 		    
//			           	Log.i(TAG,"Before SShot");
//			           	Log.i(TAG,"*******************   TADAA!!   ***************************");
//			           	Log.i(TAG,"xW = "+xW+": xH = "+xH);
//			           	Log.i(TAG,"Width = "+picTaken.getWidth()+": Height = "+picTaken.getHeight());
//			           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
//			           	Log.i(TAG,"scaleFactor = "+scaleFactor);
//			           	
//			           	Log.i(TAG,"*******************   TADAA!!   ***************************");
//			           	if(width > screenHeight || height > screenWidth){
//			           		if(width > 1280 || height > 1280){
//			           			picTaken = Bitmap.createScaledBitmap(picTaken, Math.round(width/2), Math.round( height /2),true);
//			           		}
//			           		picTaken = Bitmap.createBitmap(picTaken, 0,0,picTaken.getWidth(), picTaken.getHeight(), matrix, true);
//			           		width = picTaken.getWidth();
//				           	height = picTaken.getHeight();
//				           	extraWidth = width - screenWidth;
//				           	extraHeight = height - screenHeight;
//				           	marginalWidth = Math.round(extraWidth/2);
//				           	marginalHeight = Math.round(extraHeight/2);
//				           	if(marginalHeight < 0)
//				           		marginalHeight = 0;
//				           	if(marginalWidth < 0)
//				           		marginalWidth = 0;
//				           	if(extraWidth < 0)
//				           		extraWidth = 0;
//				           	if(extraHeight < 0)
//				           		extraHeight = 0;
//				           	
//				           	Log.i(TAG,"Width = "+width+": Height = "+height);
//				           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
//				           	Log.i(TAG,"marginalWidth = "+marginalWidth+": marginalHeight = "+marginalHeight);
//				           	
//				           	picTaken = Bitmap.createBitmap(picTaken,marginalWidth,marginalHeight,width - extraWidth, height - marginalHeight);
//			           		
//				        }
//			           	else{
//			           		picTaken = Bitmap.createBitmap(picTaken, 0,0,picTaken.getWidth(), picTaken.getHeight(), matrix, true);
//			           		width = picTaken.getWidth();
//				           	height = picTaken.getHeight();
//				           	Log.i(TAG,"Width = "+width+": Height = "+height);
//				           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
//			           	}
//			           	width = picTaken.getWidth();
//			           	height = picTaken.getHeight();
//			            picTaken = Bitmap.createBitmap(picTaken, 0,0,width, Math.round(height/2));
//			 		    settoBackground(buttonView,picTaken);
//			 		    //settoBackground(pictureLayout,picTaken);
//			 		    //settoBackground(buttonView,picTaken);
//			 		    //cumShotPreviewBottom.setVisibility(ImageView.VISIBLE);
//			 		    
//		            	
////		            	Log.i(TAG,"After SShot");
////		            	
////		            	Log.i(TAG,"Bitmap properties: Width = "+bitmap.getWidth()+": Height = "+bitmap.getHeight());
////		            	previewImage.setDrawingCacheEnabled(true);
////		            	Log.i(TAG,"1");
////		            	previewImage.buildDrawingCache();
////		            	Log.i(TAG,"2");
////		            	bitmap = previewImage.getDrawingCache();
////		            	Log.i(TAG,"3");
//		            	
//		            
//		            	
//		            	mCamera.stopPreview();
//		            	releaseCamera();
//		            	previewImage.setVisibility(ImageView.GONE);
//		            	backPic = true;
//				        
//				        if(!retakeFront)
//				        {
//				        	setSide("FRONT");
//				        	//setUntake("BACK");
//				        }
//		            	
//		            	
//		            }
//		            else
//		            {
//		            	Log.i(TAG,"Side = "+side);
//		            	setRetake(side);
//		            	matrix.postRotate(result); 
//			 		    matrix.preScale(-1, 1);
//			           	
//		            	options = new BitmapFactory.Options();
//			 	  		options.inSampleSize = 1;
//			 	  		options.inJustDecodeBounds = true;
//			 	  		
//			 	  		// Determine how much to scale down the image
//			 	  	    //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//			 	  	  
//			 	  		Bitmap temp = BitmapFactory.decodeByteArray(data, 0,  data.length);
//			 			int xW = temp.getWidth();
//			 			int xH = temp.getHeight();
//			 	  		// Determine how much to scale down the image
//			 	  	    int scaleFactor = Math.min(xW/screenHeight, xH/screenWidth);
//			 	  		// Calculate inSampleSize
//			 		    //options.inSampleSize = bitmapResizer.calculateInSampleSize(options, shortWidth, shortHeight);
//			 		    options.inSampleSize = scaleFactor;
//			 		    // Decode bitmap with inSampleSize set
//			 		    options.inJustDecodeBounds = false;
//				    		
//			 		    picTaken = BitmapFactory.decodeByteArray(data, 0,  data.length,options);
//			 		    width = picTaken.getWidth();
//			           	height = picTaken.getHeight();
//			 		    
//			           	Log.i(TAG,"Before SShot");
//			           	Log.i(TAG,"Width = "+picTaken.getWidth()+": Height = "+picTaken.getHeight());
//			           	
//			           	if(width > screenHeight || height > screenWidth){
//			           		if(width > 1280 || height > 1280){
//			           			picTaken = Bitmap.createScaledBitmap(picTaken, Math.round(width/2), Math.round( height /2),true);
//			           		}
//			           		picTaken = Bitmap.createBitmap(picTaken, 0,0,picTaken.getWidth(), picTaken.getHeight(), matrix, true);
//			           		width = picTaken.getWidth();
//				           	height = picTaken.getHeight();
//				           	extraWidth = width - screenWidth;
//				           	extraHeight = height - screenHeight;
//				           	marginalWidth = Math.round(extraWidth/2);
//				           	marginalHeight = Math.round(extraHeight/2);
//				           	if(marginalHeight < 0)
//				           		marginalHeight = 0;
//				           	if(marginalWidth < 0)
//				           		marginalWidth = 0;
//				           	if(extraWidth < 0)
//				           		extraWidth = 0;
//				           	if(extraHeight < 0)
//				           		extraHeight = 0;
//				           	
//				           	Log.i(TAG,"Width = "+width+": Height = "+height);
//				           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
//				           	Log.i(TAG,"marginalWidth = "+marginalWidth+": marginalHeight = "+marginalHeight);
//				           	Log.i(TAG,"Resizing~ ching ching!");
//				           	picTaken = Bitmap.createBitmap(picTaken,marginalWidth,marginalHeight,width - extraWidth, height - marginalHeight);
//			           		
//				        }
//			           	else{
//			           		picTaken = Bitmap.createBitmap(picTaken, 0,0,picTaken.getWidth(), picTaken.getHeight(), matrix, true);
//			           		width = picTaken.getWidth();
//				           	height = picTaken.getHeight();
//				           	Log.i(TAG,"Width = "+width+": Height = "+height);
//				           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
//				           	Log.i(TAG,"Unresized booya!");
//			           	}
//			           	width = picTaken.getWidth();
//			           	height = picTaken.getHeight();
//			           	boolean b = picTaken.isMutable();
//			           	Log.i(TAG, "The reason = "+b);
//			           	Log.i(TAG,"Flag 1");
//			            picTaken = Bitmap.createBitmap(picTaken, 0,Math.round(height/2),width, height/2);
//
//			           	Log.i(TAG,"Flag 2");
//			            settoBackground(buttonView,picTaken);
//
//			           	Log.i(TAG,"Flag 3");
//			 		    mCamera.stopPreview();
//		 	            releaseCamera();
//		 	            frontPic = true;
//			            //Toast.makeText(getApplicationContext(),"Nice one!! You could save and share your image now. :D",Field.SHOWTIME).show();
//			        	
//		            }
//		            //FileOutputStream fos = new FileOutputStream(pictureFile);
//		            //fos.write(data);
//		            //fos.close();
//		            if(backPic && frontPic){
//			    		saveable = true;
//			    		saveButton.setImageResource(R.drawable.save1);
//			    		
//			    	}
//		        }catch (Exception e) {
//		        	Log.i(TAG,"not saved");
//		        	Log.e(TAG,"Error accessing file: " + e.getMessage());
//		        	Toast.makeText(getApplicationContext(),"申し訳ありませんが、何かがカメラで間違っていた。",Field.SHOWTIME).show();
//		        	//linkSTART();
//		        	
//		        }
//	    }
//	};
//	
//	public PictureCallback mPicture = new PictureCallback() {
//
//	    @Override
//	    public void onPictureTaken(byte[] data, Camera camera) {
//	    	try {
//	    		
//	    	//Toast.makeText(getApplicationContext(),"1",1000).show();
//    		options = new BitmapFactory.Options();
// 	  		options.inSampleSize = 1;
// 	  		options.inJustDecodeBounds = true;
// 	  		
// 	  		// Determine how much to scale down the image
// 	  	    //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
// 	  	  
// 	  		Bitmap temp = BitmapFactory.decodeByteArray(data, 0,  data.length);
// 			int xW = temp.getWidth();
// 			int xH = temp.getHeight();
// 	  		// Determine how much to scale down the image
// 	  	    int scaleFactor = Math.min(xW/screenHeight, xH/screenWidth);
// 	  		// Calculate inSampleSize
// 		    //options.inSampleSize = bitmapResizer.calculateInSampleSize(options, shortWidth, shortHeight);
// 		    options.inSampleSize = scaleFactor;
// 		    // Decode bitmap with inSampleSize set
// 		    options.inJustDecodeBounds = false;
//	    		
//	    	
//	    	ImageView buttonView = getPressedPreview(side);
//	    	int width = 0;
//	    	int height = 0;
//	    	Matrix matrix = new Matrix(); 
//	    	
//	        Log.i(TAG,"Result = "+result);
//	    	//int w = previewLayout.getWidth();
//	    	//int h = previewLayout.getHeight();
//	    	mCamera.stopPreview();
//	    	Log.i(TAG,"Pic taken");
//	    	
//	        	// Image captured and saved to fileUri specified in the Intent
//				// We need to recyle unused bitmaps
//	            if (bitmap != null) {
//	              bitmap.recycle();
//	            }
//
//		    	//Toast.makeText(getApplicationContext(),"2",1000).show();
//	            if(side == "BACK")
//	            {
//	            	//if(saveable)
//	            		setRetake(side);
//	            	
//	            	matrix.postRotate(result); 
//	 	            //bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//	            	//f(android.os.Build.VERSION)
//	            	Log.i(TAG,"Version = "+android.os.Build.VERSION.SDK_INT);
//	            	if(android.os.Build.VERSION.SDK_INT >= 14)
//	            		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//	            	else
//	            		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//	            	
//	            	getProportion();
//	            	//definedWidth = screenWidth;
//	            	//definedHeight = screenHeight;
//	            	
//	            	//width = balancePhoto(definedWidth,bitmap.getWidth());
//	            	//height= balancePhoto(definedHeight,bitmap.getHeight());
//	            	//width = cameraUtility.getPicWidth();
//	            	//height = cameraUtility.getPicHeight();
//	            	//width = screenWidth;
//	            	//height= screenHeight;
//	            	if(bitmap.getHeight() > screenWidth){
//	            		width = (bitmap.getWidth() / (bitmap.getWidth()/screenWidth));
//	            		height = (bitmap.getHeight() / (bitmap.getHeight()/screenHeight));
//	            	}
//	            	else
//	            	{
//	            		width = bitmap.getWidth();
//		            	height = bitmap.getHeight();
//	            	}
//	            	
//	            	Log.i(TAG,"Bitmap properties: Width = "+bitmap.getWidth()+": Height = "+bitmap.getHeight());
//	            	
//	           
//	            	//if(width > height)
//	            		bitmap = Bitmap.createScaledBitmap(bitmap,width, height, true);
//	            	Log.i(TAG,"Before Rotate");  
//	            	bitmap = bitmap.createBitmap(bitmap, 0, 0,width,height, matrix, true);
//	            	Log.i(TAG,"After Rotate");
//	            	Log.i(TAG,"1");
//	 	            //width = balancePhoto(preview.getWidth(),bitmap.getWidth());
//	 	            //height= balancePhoto(preview.getHeight(),bitmap.getHeight());//preview.getHeight();//balancePhoto(screenHeight,bitmap.getHeight());
//	 	            //width = bitmap.getWidth();
//	 	            //height= bitmap.getHeight();
//	 		    	//Toast.makeText(getApplicationContext(),"3",1000).show();
//	 	            //Log.i(TAG,"BACK ... "+width+":"+height+"   =   "+bitmap.getWidth()+":"+bitmap.getHeight());
//	 	            Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,height, Math.round(width/2));
//	            	//Bitmap bmp=bitmap;
//	 	            //bitmap.
//	 	            Log.i(TAG,"2");
//	 	            Log.i(TAG,"Bitmap properties: Width = "+width+": Height = "+height);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,definedWidth, Math.round(definedHeight/2));
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,width,height);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(),width);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(width/2), height);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(width/2), height, matrix, true);
//	 	            BitmapDrawable bd = new BitmapDrawable(bmp);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(bitmap.getWidth() /2)+1, bitmap.getHeight());
//	 	            //buttonView.setImageBitmap(bmp);
//	 	            //buttonView.setBackground(bd);
//	 	            
//	 	            
//	 	            int sdk = android.os.Build.VERSION.SDK_INT;
//	 	            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//	 	        	   buttonView.setBackgroundDrawable(bd);
//	 	            } else {
//	 	        	   buttonView.setBackground(bd);
//	 	            }
//	 	            
//	 	            //imageView.setImageBitmap(bitmap);
//	 	            Log.i(TAG,"To be saved2");
//	 	            Log.i(TAG,"Bitmap properties: Width = "+bitmap.getWidth()+": Height = "+bitmap.getHeight());
//	 	            Log.i(TAG,"bmp properties: Width = "+bmp.getWidth()+": Height = "+bmp.getHeight());
//	 	            //Toast.makeText(getApplicationContext(),"Bitmap properties: Width = "+bitmap.getWidth()+": Height = "+bitmap.getHeight()+"/nbmp properties: Width = "+bmp.getWidth()+": Height = "+bmp.getHeight(),Field.SHOWTIME).show();
//		 	      	
//	            	
//	            	releaseCamera();
//	            	backPic = true;
//	            	bitmap = null;
//			        bmp = null;	 
//			        
//			        if(!retakeFront)
//			        {
//			        	setSide("FRONT");
//			        	//setUntake("BACK");
//			        }
//	            	
//	            	
//	            }
//	            else
//	            {
//	            	//if(saveable)	
//	            		setRetake(side);
//	            	
//	            	matrix.postRotate(result); 
//
//	            	
//	 	            //matrix.postRotate(270); 
//	 	            //bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//	 	            //bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//	    	        if(android.os.Build.VERSION.SDK_INT >= 14)
//	            		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//	            	else
//	            		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//	    	        
//	    	        //width = balancePhoto(definedWidth,bitmap.getWidth());
//	            	//height= balancePhoto(definedHeight,bitmap.getHeight());
//	    	        //width = cameraUtility.getCamWidth();
//	            	//height = cameraUtility.getCamHeight();
//	    	        if(bitmap.getHeight() > screenWidth){
//	            		width = (bitmap.getWidth() / (bitmap.getWidth()/screenWidth));
//	            		height = (bitmap.getHeight() / (bitmap.getHeight()/screenHeight));
//	            	}
//	            	else
//	            	{
//	            		width = bitmap.getWidth();
//		            	height = bitmap.getHeight();
//	            	}
//	            	Log.i(TAG,"Bitmap properties: Width = "+bitmap.getWidth()+": Height = "+bitmap.getHeight());
//	            	
//	            	//if(width > height)
//	            		bitmap = Bitmap.createScaledBitmap(bitmap,width, height, true);
//	           
//	            	bitmap = bitmap.createBitmap(bitmap, 0, 0,width,height, matrix, true);
//	            	Log.i(TAG,"Bitmap properties: Width = "+bitmap.getWidth()+": Height = "+bitmap.getHeight());
//	 	            //width = balancePhoto(screenWidth,bitmap.getWidth());
//	 	            //height= screenHeight;//balancePhoto(screenHeight,bitmap.getHeight());
//	 	            //width = bitmap.getWidth();
//	 	            //height= bitmap.getHeight();
//	 	            Log.i(TAG,width+":"+height+"   =   "+bitmap.getWidth()+":"+bitmap.getHeight());
//	 	            //Log.i(TAG,topL.getWidth()+":"+ topL.getHeight()+"   =   "+bitmap.getWidth()+":"+bitmap.getHeight());
//	 	            //matrix = new Matrix(); 
//	 	            matrix.postRotate(result); 
//	            	matrix.preScale(-1, 1);
//	            	Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,height, Math.round(width/2), matrix, true);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,width, Math.round(height/2), matrix, true);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,definedWidth, Math.round(definedHeight/2));
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), width, matrix, true);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,width, Math.round(height/2));
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(width/2), height, matrix, true);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(bitmap.getWidth() /2)+1, bitmap.getHeight());
//	 	            BitmapDrawable bd = new BitmapDrawable(bmp);
//	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(bitmap.getWidth() /2)+1, bitmap.getHeight());
//	 	            //buttonView.setImageBitmap(bmp);
//	 	            //buttonView.setBackground(bd);
//	 	            //buttonView.setImageBitmap(bmp);
//	 	            int sdk = android.os.Build.VERSION.SDK_INT;
//	 	            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//	 	        	   buttonView.setBackgroundDrawable(bd);
//	 	            } else {
//	 	        	   buttonView.setBackground(bd);
//	 	            }
//	 	            Log.i(TAG,"To be saved3");
//	 	            Log.i(TAG,"Bitmap properties: Width = "+bitmap.getWidth()+": Height = "+bitmap.getHeight());
//	 	            Log.i(TAG,"bmp properties: Width = "+bmp.getWidth()+": Height = "+bmp.getHeight());
//	 	            //Toast.makeText(getApplicationContext(),"Bitmap properties: Width = "+bitmap.getWidth()+": Height = "+bitmap.getHeight()+"/nbmp properties: Width = "+bmp.getWidth()+": Height = "+bmp.getHeight(),Field.SHOWTIME).show();
//	 	            
//	 	            releaseCamera();
//	 	            frontPic = true;
//	 	            bitmap = null;
//			        bmp = null;	 
//		            //Toast.makeText(getApplicationContext(),"Nice one!! You could save and share your image now. :D",Field.SHOWTIME).show();
//		        	
//	            }
//	            //FileOutputStream fos = new FileOutputStream(pictureFile);
//	            //fos.write(data);
//	            //fos.close();
//	            if(backPic && frontPic){
//		    		saveable = true;
//		    		saveButton.setImageResource(R.drawable.save1);
//		    		
//		    	}
//	        }catch (Exception e) {
//	        	Log.i(TAG,"not saved");
//	        	Log.e(TAG,"Error accessing file: " + e.getMessage());
//	        	Toast.makeText(getApplicationContext(),"申し訳ありませんが、何かがカメラで間違っていた。",Field.SHOWTIME).show();
//	        	//linkSTART();
//	        	
//	        }
//	        
//	    }
//	    
//	};
//	
//	public int balancePhoto(int viewSize, int bitmapSize){
//		if(viewSize > bitmapSize)
//			//return Math.round(bitmapSize / viewSize) * 160;
//			return bitmapSize;
//		else		
//			return viewSize;
//	}
//	
//	public void getProportion(){
//		Log.i(TAG,"ScreenWidth = "+screenWidth+" : ScreenHeight = "+screenHeight);
//		Log.i(TAG,"mCameraW = "+cameraUtility.getCamWidth()+" : mCameraH = "+cameraUtility.getCamHeight());
//		Log.i(TAG,"BitmapWidth = "+bitmap.getWidth()+" : BitmapHeight = "+bitmap.getHeight());
//		Log.i(TAG,"cumShotPreviewTop = "+cumShotPreviewTop.getWidth()+" : cumShotPreviewTop = "+cumShotPreviewTop.getHeight());
//		
//	}
//
//	public void saveImage(Bitmap bmp){
//		try {
//				
//			   
//	           //File pictureFile = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE);
//			   //String fileName = 
//			   Log.d(TAG,"the filename = "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
//			   fileName = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString();
//			   Log.d(TAG,"The utility = "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
//		       FileOutputStream out = new FileOutputStream(mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE));
//		       Log.d(TAG,"Before saving");
//		       bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
//		       Log.d(TAG,"After saving");
//		       mediaUtility.updateMedia(TAG,"file://" +mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
//		       Log.d(TAG,"file://" +mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
//		       out.flush();
//		       out.close();
//		       Log.d(TAG,"Saved to "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
//		       Toast.makeText(getApplicationContext(),"写真の保存が完了しました。",Field.SHOWTIME).show();
//		       saved = true;
//		       shareButton.setImageResource(R.drawable.share1);
//		  
//		} catch (Exception e) {
//		       e.printStackTrace();
//		       Log.d(TAG,"Saving failed cause = "+ e.getCause() );
//		       Toast.makeText(getApplicationContext(),"申し訳ありませんが、何かがカメラで間違っていた。",Field.SHOWTIME).show();
//				
//		}
//	}
//	
//	public ImageView getPressedPreview(String side){
//		ImageView buttonView = null;
//		
//		if(side == "BACK")
//			buttonView = cumShotPreviewTop;
//        if(side == "FRONT")
//        	buttonView = cumShotPreviewBottom;
//        
//		return buttonView;
//	}
//	
//	
//	public void seePreview(String side){
//		try{
//
//        	previewImage.setVisibility(ImageView.GONE);
//			preview.setVisibility(FrameLayout.VISIBLE);
//			saveable = false;	
//			saveButton.setImageResource(R.drawable.save2);
//			releaseCamera();
//			setUntake(side);
//			ImageView buttonView = getPressedPreview(side);
//			cameraUtility = new CameraUtility(getApplicationContext());
//			Log.i(TAG, "1");
//			mCamera = cameraUtility.getCameraInstance(side,screenHeight,screenWidth,"PORTAIT");
//			Log.i(TAG, "a");
//			setCameraDisplayOrientation(this,cameraUtility.findCamera(side),mCamera);
//			Log.i(TAG, "b");
//			mPreview = new CameraPreview(getApplicationContext(), mCamera);
//			preview.removeAllViews();
//			preview.addView(mPreview);
//			Log.i(TAG, "2");
//	
//			buttonView.setBackgroundDrawable(null);
//			buttonView.setImageBitmap(null);
//				
//		}catch(Exception e){
//			Log.e(TAG,"Di ko na alam to wtf ftw");
//			Log.e(TAG,"e = "+e.getCause());
//			
//			//Toast.makeText(getApplicationContext(),"OOPS!! Error = "+e.getMessage(),Field.SHOWTIME).show();
//			Toast.makeText(getApplicationContext(),"申し訳ありませんが、何かがカメラで間違っていた。",Field.SHOWTIME).show();
//        	//linkSTART();
//		}
//	}
//	
//	public void setOrientation(){
//		 CameraInfo info = new CameraInfo();
//		 int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
//	     int degrees = 0;
//	     switch (rotation) {
//	         case Surface.ROTATION_0: degrees = 0; break;
//	         case Surface.ROTATION_90: degrees = 90; break;
//	         case Surface.ROTATION_180: degrees = 180; break;
//	         case Surface.ROTATION_270: degrees = 270; break;
//	     }
//	     
//	     int result;
//	     Log.i(TAG,"Degrees = "+degrees);
//	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//	         result = (info.orientation ) % 360;
//	         result = (360 - result) % 360;  // compensate the mirror
//	     } else {  // back-facing
//	         result = (info.orientation - degrees + 360+ 90) % 360;
//	     }
//	     boolean a = mCamera==null;
//	     Log.d(TAG,"mCamera = "+a);
//	     
//		mCamera.setDisplayOrientation(result);
//	}
//	
//	
//	//123
//	public static void setCameraDisplayOrientation(Activity activity,
//	         int cameraId, android.hardware.Camera camera) {
//		 Parameters params;
//		 int width = 0;
//		 int height = 0;
//	     android.hardware.Camera.CameraInfo info =
//	             new android.hardware.Camera.CameraInfo();
//	     android.hardware.Camera.getCameraInfo(cameraId, info);
//	     int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//	     
//	     switch (rotation) {
//	         case Surface.ROTATION_0: degrees = 0; break;
//	         case Surface.ROTATION_90: degrees = 90; break;
//	         case Surface.ROTATION_180: degrees = 180; break;
//	         case Surface.ROTATION_270: degrees = 270; break;
//	     }
//
//	    
//	     Log.i(TAG,"Degrees = "+degrees);
//	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//	         result = (info.orientation + degrees) % 360;
//	         result = (360 - result) % 360;  // compensate the mirror
//	     } else {  // back-facing
//	         result = (info.orientation - degrees + 360) % 360;
//	     }
//	     
//	     width = cameraUtility.getCamWidth();
//     	 height = cameraUtility.getCamHeight();
//
//	     Log.i(TAG,"width "+width);
//     	 params = camera.getParameters();
//     	 params.setPreviewSize(width,height);
//
//	     camera.setDisplayOrientation(result);
//	     camera.setParameters(params);
//	     Log.i(TAG,"123");
//	 }
//	/*
//	private Camera.Size getBestPreviewSize(List<Camera.Size> previewSizes, int width, int height) {
//        double targetAspect = (double)width / (double)height;
//
//		ArrayList<Camera.Size> matchedPreviewSizes = new ArrayList<Camera.Size>();
//		final double ASPECT_TOLERANCE = 0.1;
//		for(Size previewSize : previewSizes) {
//		        double previewAspect = (double)previewSize.width / (double)previewSize.height;
//		
//		        // Original broken code.
//		        //if(Math.abs(targetAspect - previewAspect) < ASPECT_TOLERANCE) {
//		        //        matchedPreviewSizes.add(previewSize);
//		        //}
//		
//		        // Working code.
//		        if(Math.abs(targetAspect - previewAspect) < ASPECT_TOLERANCE &&
//		                    previewSize.width <= width && previewSize.height <= height) {
//		                matchedPreviewSizes.add(previewSize);
//		        }
//		}
//		
//		Camera.Size bestPreviewSize;
//		if(!matchedPreviewSizes.isEmpty()) {
//		        bestPreviewSize = Collections.max(matchedPreviewSizes, sizeComparator);
//		} else {
//		        bestPreviewSize = Collections.max(previewSizes, sizeComparator);
//		}
//		
//		return bestPreviewSize;
//		}
//	*/
//	
//	@Override
//    protected void onPause() {
//        super.onPause();
//        Log.i(TAG, "Destroying onPause");
//        //relenquishTheSoul();
//    }
//	
//	@Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG, "Destroying onDestroy");
//        //relenquishTheSoul();
//        
//    }
//	
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//	    //super.onConfigurationChanged(newConfig);
//		
//	    // Checks the orientation of the screen
//	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//	        Toast.makeText(CopyOfDualCamActivity.this, "landscape", Field.SHOWTIME).show();
//	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//	        Toast.makeText(CopyOfDualCamActivity.this, "portrait", Field.SHOWTIME).show();
//	    }
//	  }
//
//	
//	public void relenquishTheSoul(){
//		releaseCamera();              // release the camera immediately on pause event
//        options = null;
//        if(bitmap != null)
//        	bitmap.recycle();
//        if(picTaken != null)
//        	picTaken.recycle();
//        bitmap = null;
//        finish();
//        System.exit(0);
//	}
//	 
//	public void releaseCamera(){
//        if (mCamera != null){
//            mCamera.release();        // release the camera for other applications
//            mCamera = null;
//        }
//    }
//	
//	public void linkSTART(){
//		finish();
//		Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
//		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(i);
//	}
//	
//	public void shareFunction(){
//		//"android.resource://" + getPackageName() + "/" +
//		Uri uri = Uri.parse("file://"+fileName);
//
//		//Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" +R.drawable.icon);
//		String shareBody = "Here is the share content body";
//		sharingIntent = new Intent(Intent.ACTION_SEND);
//		sharingIntent.setType("image/png");
//		//sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
//		//sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//		sharingIntent.putExtra(Intent.EXTRA_STREAM,uri);
//		startActivity(Intent.createChooser(sharingIntent, "Share via"));
//	}
//	
//	public void takeAShot(){
//		if(mCamera != null){
//			//Toast.makeText(getApplicationContext(),"Before Nice shot!",Field.SHOWTIME).show();
//			//mCamera.takePicture(null, null, mPictureS3FIX);
//			//s3Fix();
//			try{
//				//mCamera.takePicture(null, null, s3FixIloveS3);
//				mCamera.setErrorCallback(ec);
//				//mCamera.takePicture(null, null, mPicture);
//				mCamera.takePicture(null, null,getPic);
//				//Toast.makeText(getApplicationContext(),"素晴らしい!!",Field.SHOWTIME).show();
//				//Log.i(TAG, "Before SShot");
//				//SShot();
//			}catch(Exception e){
//				//mCamera.takePicture(null, null, s3FixIloveS3);
//				Log.i(TAG, "Error at smiley button : e = "+e.getCause());
//				Toast.makeText(getApplicationContext(),"申し訳ありませんが、何かがカメラで間違っていた。",Field.SHOWTIME).show();
//			}
//			
//		}
//	}
//
//	public void createAlert(String currentFunction,String thisside,String thismessage){
//		final String title = currentFunction;
//		final String side = thisside;
//		final String message = thismessage;
//		
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//				CopyOfDualCamActivity.this);
// 
//			// set title
//			//alertDialogBuilder.setTitle(title);
// 
//			// set dialog message
//			alertDialogBuilder
//				.setMessage(message)
//				.setCancelable(false)
//				.setPositiveButton("はい ",new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,int id) {
//						if(title == "retake"){
//							Log.i(TAG, "Initiating Retake :D");
//							setSide(side);
//						}
//					}
//				  })
//				.setNegativeButton("いいえ",new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,int id) {
//						
//					}
//				});
// 
//			// create alert dialog
//			AlertDialog alert = alertDialogBuilder.create();
//	    	alert.show();
//	    	
//	}
//	
//	public void settoBackground(View view, Bitmap bitmap){
//		 BitmapDrawable bd = new BitmapDrawable(bitmap);
//		 int sdk = android.os.Build.VERSION.SDK_INT;
//         if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//     	   view.setBackgroundDrawable(bd);
//         } else {
//     	   view.setBackground(bd);
//         }
//	}
//
//	
//	private SensorEventListener mySensorEventListener = new SensorEventListener() {
//
//	    @Override
//	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//	    	
//	    }
//
//	    public void onSensorChanged(SensorEvent event) {
//
//	        float azimuth = event.values[0];
//	        Matrix matrix=new Matrix();
//	        Matrix matrix2=new Matrix();
//	        matrix2.setScale((float) 0.5,(float) 0.5);
//	       
//	         int sdk = android.os.Build.VERSION.SDK_INT;
//	         if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//	        	//Portrait
//	 	        //Log.i(TAG,"Azimuth = "+azimuth);
//	 	        if(((azimuth) >= 315 && (azimuth) <= 360) || ((azimuth) >= 0 && (azimuth) <= 15)){
//	 	        	matrix.postRotate(0,Math.round(smileyButton.getWidth()/2),Math.round( smileyButton.getHeight()/2));
//	 	        	//matrix3.postRotate(0,Math.round(cumShotPreviewBottom.getWidth()/2),Math.round(cumShotPreviewBottom.getHeight()/2));
//	 	        	
//	 	        }
//	 	        
//	 	        //Normal Landscape
//	 	        else if((azimuth) >= 230 && (azimuth) <= 315){
//	 	        	matrix.postRotate(270,Math.round(smileyButton.getWidth()/2),Math.round( smileyButton.getHeight()/2));
//	 	        	//matrix3.postRotate(270,Math.round(cumShotPreviewBottom.getWidth()/2),Math.round(cumShotPreviewBottom.getHeight()/2));
//	 	        	//cumShotPreviewBottom.setImageResource(R.drawable.previewfrontlandscape);
//	 	        }
//	 	        
//	 	        //Reverse Landscape
//	 	        else if((azimuth) >= 15 && (azimuth) <= 115){
//	 	        	matrix.postRotate(90,Math.round(smileyButton.getWidth()/2),Math.round( smileyButton.getHeight()/2));
//	 	        	//matrix3.postRotate(90,Math.round(cumShotPreviewBottom.getWidth()/2),Math.round(cumShotPreviewBottom.getHeight()/2));
//	 	        	//cumShotPreviewBottom.setImageResource(R.drawable.previewfrontlandscape);
//	 	        }
//	 	        
//	 	        //Portrait
//	 	        if(((azimuth) >= 315 && (azimuth) <= 360) || ((azimuth) >= 0 && (azimuth) <= 15))
//		        	matrix2.postRotate(0,Math.round(saveButton.getWidth()/2),Math.round(saveButton.getHeight()/2));
//	 	        
//	 	        //Normal Landscape
//		        else if((azimuth) >= 230 && (azimuth) <= 315)
//		        	matrix2.postRotate(270,Math.round(saveButton.getWidth()/2),Math.round(saveButton.getHeight()/2));
//	 	        
//	 	        //Reverse Landscape
//		        else if((azimuth) >= 15 && (azimuth) <= 115)
//		        	matrix2.postRotate(90,Math.round(saveButton.getWidth()/2),Math.round(saveButton.getHeight()/2));
//		        
//	         } else {
//	        	//Portrait
//	 	        //Log.i(TAG,"Azimuth = "+azimuth);
//	 	        if(((azimuth) >= 315 && (azimuth) <= 360) || ((azimuth) >= 0 && (azimuth) <= 15))
//	 	        	matrix.postRotate(0,Math.round(smileyButton.getWidth()/2),Math.round( smileyButton.getHeight()/2));
//	 	        
//	 	        //Normal Landscape
//	 	        else if((azimuth) >= 230 && (azimuth) <= 315)
//	 	        	matrix.postRotate(90,Math.round(smileyButton.getWidth()/2),Math.round( smileyButton.getHeight()/2));
//	 	        
//	 	        //Reverse Landscape
//	 	        else if((azimuth) >= 15 && (azimuth) <= 115)
//	 	        	matrix.postRotate(270,Math.round(smileyButton.getWidth()/2),Math.round( smileyButton.getHeight()/2));
//	 	        
//	 	        
//	 	        //Portrait
//	 	        if(((azimuth) >= 315 && (azimuth) <= 360) || ((azimuth) >= 0 && (azimuth) <= 15))
//		        	matrix2.postRotate(0,Math.round(saveButton.getWidth()/2),Math.round(saveButton.getHeight()/2));
//	 	        
//	 	        //Normal Landscape
//		        else if((azimuth) >= 230 && (azimuth) <= 315)
//		        	matrix2.postRotate(90,Math.round(saveButton.getWidth()/2),Math.round(saveButton.getHeight()/2));
//	 	        
//	 	        //Reverse Landscape
//		        else if((azimuth) >= 15 && (azimuth) <= 115)
//		        	matrix2.postRotate(270,Math.round(saveButton.getWidth()/2),Math.round(saveButton.getHeight()/2));
//		        
//		        
//	         }
//	         
//	        //cumShotPreviewBottom.setScaleType(ScaleType.MATRIX);  
//	        //cumShotPreviewBottom.setImageMatrix(matrix3);
//	        
//	        smileyButton.setScaleType(ScaleType.MATRIX); 
//	        smileyButton.setImageMatrix(matrix);
//	         
//	        saveButton.setScaleType(ScaleType.MATRIX); 
//	        saveButton.setImageMatrix(matrix2);
//	        
//	        retryButton.setScaleType(ScaleType.MATRIX); 
//	        retryButton.setImageMatrix(matrix2);
//	        
//	        shareButton.setScaleType(ScaleType.MATRIX); 
//	        shareButton.setImageMatrix(matrix2);
//	      }
//	  };
//	  
//	  
//	  public void setRetake(String side){
//		  
//		  
//		  if(side == "BACK"){
//			  retakeBack = true;
//			  retakeBackButton.setImageResource(R.drawable.previewback);
//			
//		  }
//		  else if(side == "FRONT"){
//			  retakeFront = true;
//			  retakeFrontButton.setImageResource(R.drawable.previewfront);
//		  }
//	  }
//	  
//	  public void setUntake(String side){
//		  if(side == "BACK"){
//			  backPic = false;
//			  retakeBack = false;
//			  retakeBackButton.setImageResource(R.drawable.previewback2);
//			
//		  }
//		  else if(side == "FRONT"){
//			  frontPic = false;
//			  retakeFront = false;
//			  retakeFrontButton.setImageResource(R.drawable.previewfront2);
//		  }
//	  }
//	  
//	  public void setSide(String thisside){
//		  side = thisside;
//		  seePreview(side);
//	  }
//	  
//	  public void retakeImage(String thisside){
//		  side = thisside;
//		  String message = "写真を撮りなおしますか？";
//		  String title = "retake";
//		  createAlert(title,side,message);
//	  }
//}
//
//
////写真を撮りなおしますか？ 
////Yes はい 
////No  いいえ
