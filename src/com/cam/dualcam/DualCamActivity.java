package com.cam.dualcam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.TextView;

import android.view.OrientationEventListener;


import com.cam.dualcam.utility.*;
import com.cam.dualcam.utility.ColorPickerDialog.*;
import com.cam.dualcam.bitmap.*;
import com.cam.dualcam.view.*;

@SuppressLint("NewApi")
public class DualCamActivity extends Activity implements OnClickListener, OnColorChangedListener {
	
	//Defined variables
	//Jap Messages
	private String errorMessage			= "申し訳ありませんが、何かがカメラで間違っていた。";
	private String retakeMessage		= "写真を撮りなおしますか？";
	private String yes 					= "はい ";
	private String no 					= "いいえ";
	
	public static String TAG 			= "DualCamActivity";
	private String fileName				= null;
	private String cameraSide 			= null;
	private String orientationScreen	= null;
	
	private Bitmap tempPic				= null;
	private Bitmap frontPic 			= null;
	private Bitmap backPic  			= null;
	public BitmapFactory.Options options= null;
	
	private boolean isBackTaken 		= false;
	private boolean isFrontTaken		= false;
	private boolean isSaved				= false;
	private boolean isSavable			= false;
	private boolean isTextEditable 		= false;
	private boolean isTextAdded	 		= false;
	private boolean isRetryable			= false;
	private boolean isSavePathset		= false;
	private boolean isZoomSupported		= false;
	private boolean isSmoothZoomSupported		= false;
	
	private boolean isDoubleTapAction 	= false;
	private boolean isReadyToShoot		= false;
	private boolean isCameraFocused 	= false;
	private boolean isRetaking		 	= false;
	private boolean hasCameraFocus 		= false;
	private boolean isConfigChanging	= false;
	
	public Integer screenHeight;
	public Integer screenWidth;
	
	public static int result = 0;
	public static int degrees = 0;
	public static int orientationOfPhone = 0;
	public static int sdk = android.os.Build.VERSION.SDK_INT;
	
	//Utility
	public PackageCheck packageCheck;
	public static MediaUtility mediaUtility;
	public static CameraUtility cameraUtility;
	public static HideAct hideAct;
	public static ColorPickerDialog colorPickerDialog;
	public Intent sharingIntent;
	
	//Camera Settings
	public Parameters param;
	public Camera mCamera;
	public Integer maxCameraZoom;
	public Integer currentCameraZoom;
	//Widgets
	
	//Previews
	public ImageView backPreview
					,frontPreview
					,previewImage;
	
	public LinearLayout pictureLayout
					   ,utilityLayout;
	public RelativeLayout toSaveLayout;
	public FrameLayout   mainPreview
						,createTextFrameLayout;
    public CameraPreview cameraPreview;
    
    //Buttons
    public ImageView captureButton
    				,textButton
    				,saveButton
    				,retryButton
    				,shareButton
    				,focusMarker;
 	
    
    //Touch and click events
    public Integer bottomTapCount = 0;
    public Integer topTapCount = 0;
    public Integer tapCount = 0;
    public Integer touchCount;
    public Integer tCount;
    public Integer performedAction;
    public Integer touchAction;
    public Integer firstPointer;
    public Integer firstPointerIndex;
    public Integer secondPointer;
    public Integer secondPointerIndex;
    public Float   pointerDistance;
    public Float   changedPointerDistance;
    
    public CountDownTimer longClickTimer;
    public CountDownTimer cameraFocusTimer;
    public CountDownTimer doubleTapTimer;
    
    //For additional Features
    //Add text
    public static int fontSize = 1;
    public static String textToShow;
    public TextView addedText;
    public static int charCount;
    public RelativeLayout.LayoutParams textFrameLayoutParams;
    
    //Changes by Aid
	public static int fontColor;  //aid
	private static final String COLOR_PREFERENCE_KEY = "color";  //aid
    private static int initialColor;
    private int addedTextX = 0;
    private int addedTextY = 0;
    private int defaultTextSize = 40;
    private int minimumTextSize = 31;
    private int maximumTextSize = 79;
    //Changes by Aid
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dualcam);

		
		
		mediaUtility 	= new MediaUtility(getApplicationContext());
		packageCheck 	= new PackageCheck(getApplicationContext());

		captureButton 	= (ImageView) findViewById(R.id.smileyButton);
		textButton		= (ImageView) findViewById(R.id.textButton);
		saveButton   	= (ImageView) findViewById(R.id.saveButton);
		retryButton  	= (ImageView) findViewById(R.id.retryButton);
		shareButton  	= (ImageView) findViewById(R.id.shareButton);
		backPreview 	= (ImageView) findViewById(R.id.cumPreviewBack);
		frontPreview 	= (ImageView) findViewById(R.id.cumPreviewFront);
		previewImage 	= (ImageView) findViewById(R.id.previewImage);
		
		mainPreview 	= (FrameLayout) findViewById(R.id.cumshot);
		pictureLayout 	= (LinearLayout) findViewById(R.id.picLayout);
		//utilityLayout   = (LinearLayout) findViewById(R.id.utilityButtonLayout);
		createTextFrameLayout = (FrameLayout) findViewById(R.id.createTextFrame);
		toSaveLayout 	= (RelativeLayout)findViewById(R.id.createTextLayout);
		
		//hideAct = new HideAct(getApplicationContext(),saveButton);
		captureButton.setOnClickListener(this);
		textButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		retryButton.setOnClickListener(this);
		shareButton.setOnClickListener(this);
		//utilityLayout.setOnClickListener(this);

		backPreview.setOnTouchListener(new setTouchMode());
		frontPreview.setOnTouchListener(new setTouchMode());
		
		try{
			orientationOfPhone = this.getResources().getConfiguration().orientation;
			screenHeight = new PhoneChecker(this).screenHeight;
			screenWidth = new PhoneChecker(this).screenWidth;
			doubleTapTimer = doDoubleTap();
			touchAction = Field.ActionNothing;
			
			if(orientationOfPhone == Configuration.ORIENTATION_PORTRAIT){
				orientationScreen = "PORTRAIT";
			}
			else if(orientationOfPhone == Configuration.ORIENTATION_LANDSCAPE){
				orientationScreen = "LANDSCAPE";
			}
			else{
				orientationScreen = "UNKNOWN";
			}
			
			if (savedInstanceState != null) {
				
				
				isBackTaken 		= savedInstanceState.getBoolean("isBackTaken", isBackTaken);
				isFrontTaken 		= savedInstanceState.getBoolean("isFrontTaken", isFrontTaken);
				isSavable			= savedInstanceState.getBoolean("isSavable", isSavable);
				isSaved				= savedInstanceState.getBoolean("isSaved", isSaved);
				isTextEditable		= savedInstanceState.getBoolean("isTextEditable", isTextEditable);
				isTextAdded			= savedInstanceState.getBoolean("isTextAdded", isTextAdded);
				isRetryable			= savedInstanceState.getBoolean("isRetryable", isRetryable);
				cameraSide			= savedInstanceState.getString("cameraSide");
				fontSize			= savedInstanceState.getInt("fontSize");
				isConfigChanging	= false;
				
				
				topTapCount = 0;
				bottomTapCount = 0;
				
				if(isBackTaken){
					getPressedPreview("BACK").setVisibility(ImageView.VISIBLE);
					getPressedPreview("BACK").setBackgroundDrawable(null);
					getPressedPreview("BACK").setImageBitmap(null);
					backPic = savedInstanceState.getParcelable("backPic");
					settoBackground(getPressedPreview("BACK"), backPic);
				}

				
				if(isFrontTaken){
					getPressedPreview("FRONT").setVisibility(ImageView.VISIBLE);
					getPressedPreview("FRONT").setBackgroundDrawable(null);
					getPressedPreview("FRONT").setImageBitmap(null);
					frontPic = savedInstanceState.getParcelable("frontPic");
					settoBackground(getPressedPreview("FRONT"), frontPic);
				}
				
				if(isTextAdded){
					//Log.i(TAG, "The epic Text = "+isTextAdded+" and the textToShow is ="+textToShow);
					textToShow	= savedInstanceState.getString("textToShow").toString();
					createTextFrameLayout.removeAllViews();
					createAText();
				}
				
				if(isBackTaken && !isFrontTaken){
					setSide("FRONT");
				}
				else if(!isBackTaken && isFrontTaken){
					setSide("BACK");
				}
				else if(!isBackTaken && !isFrontTaken){
					setSide("BACK");
				}
				
				setButtons(isSaved,isSavable,isTextEditable,isRetryable);
				
			}
			else
				setSide("BACK");
		}catch(Exception e){
			Log.e(TAG, "Something went wrong insid reorientation");
		}

//		if (savedInstanceState != null) {
//			tempPic = savedInstanceState.getParcelable("bitmap");
//			settoBackground(getPressedPreview("BACK"), tempPic);
//		}
//		else
			//setSide("BACK");
	}
    
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Field.FILE_SELECT_CODE:
            if (resultCode == RESULT_OK) {
            	try {
                // Get the Uri of the selected file 
                Uri uri = data.getData();
                Log.d(TAG, "File Uri: " + uri.toString());
                // Get the path
                fileName = mediaUtility.getPath(getApplicationContext(), uri);
                isSavePathset = true;
                Log.d(TAG, "File Path: " + fileName);
                linkSTART();
                // Get the file instance
                // File file = new File(path);
                // Initiate the upload
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            break;
            
            case DirectoryPicker.PICK_DIRECTORY:
            	if(resultCode == RESULT_OK) {
            		Bundle extras = data.getExtras();
            		fileName = (String) extras.get(DirectoryPicker.CHOSEN_DIRECTORY);
            		isSavePathset = true;
            		// do stuff with path
            	}
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
    public void onClick(View view) {
		
		try{
			if(view.getId() == R.id.smileyButton){
				//setFocusMarker("FOCUS", null,null,null);
				if(isCameraFocused)
					takeAShot();
				
			}
			
			else if(view.getId() == R.id.textButton){
				
				//if(isSavable)
					try{
						if(isTextEditable)
							setEditText();
					}catch(Exception e){
						Toast.makeText(getApplicationContext(),errorMessage,Field.SHOWTIME).show();
					}
				//else
					//Toast.makeText(getApplicationContext(),"You don't want a pic of yourself?",Field.SHOWTIME).show();
				
			}
			
			else if(view.getId() == R.id.saveButton){
				
				if(isSavable)
					try{
						toSaveLayout.buildDrawingCache();
						saveImage(toSaveLayout.getDrawingCache());
						toSaveLayout.destroyDrawingCache();
						//saveImage();
					}catch(Exception e){
						Toast.makeText(getApplicationContext(),errorMessage,Field.SHOWTIME).show();
					}
				//else
					//Toast.makeText(getApplicationContext(),"You don't want a pic of yourself?",Field.SHOWTIME).show();
				
			}
			
			else if(view.getId() == R.id.retryButton){
				if(isRetryable)
//				AlertDialog.Builder retryDialog = new AlertDialog.Builder(DualCamActivity.this);
//		          
//		          // set title
//				retryDialog.setTitle("Restart App");
//		      
//		          // set dialog message
//				retryDialog
//		            .setMessage("Restart from the beginning?")
//		            .setCancelable(false)
//		            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
//		              public void onClick(DialogInterface dialog,int id) {
		            	  linkSTART();
//		              }
//		              })
//		            .setNegativeButton("No",new DialogInterface.OnClickListener() {
//		              public void onClick(DialogInterface dialog,int id) {
//		            	  
//
//		              }
//		            });
//		      
//		          // create alert dialog
//		          AlertDialog alert = retryDialog.create();
//		            alert.show();
				
				
			}
			
			else if(view.getId() == R.id.shareButton){
				try{
					Log.i(TAG, "isSaved = "+isSaved);
					if(isSaved)
						shareFunction();
					
				}catch(Exception e){
					Log.i(TAG, "isSaved = "+isSaved);
					Log.i(TAG,"ERROR = "+e.getCause());
				}
				//else
					//Toast.makeText(getApplicationContext(),"画像を保存してください",Field.SHOWTIME).show();
				
			}

			
			else if(view.getId() == R.id.cumPreviewBack){
				Log.i(TAG, ".cumPreviewBack is clicked");
				if(isSavable){
					//setSide("BACK");
					retakeImage("BACK");
					//createAlert("","","");
					
				}
				else if(isBackTaken  && !isFrontTaken){
					retakeImage("BACK");
					ImageView buttonView = getPressedPreview("FRONT");
					buttonView.setImageDrawable(getResources().getDrawable(R.drawable.previewfront));
				}
				else{
					if(cameraSide == "BACK")
						takeAShot();
				}
//				else{
//					mCamera.autoFocus(new AutoFocusCallback(){
//						@Override
//						public void onAutoFocus(boolean arg0, Camera arg1) {
//							//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//						}
//					});
//				}
			}
			
			else if(view.getId() == R.id.cumPreviewFront){
				if(isSavable){
					//setSide("FRONT");
					retakeImage("FRONT");
				}
				else{
					if(cameraSide == "FRONT")
						takeAShot();
				}
//				else{
//					mCamera.autoFocus(new AutoFocusCallback(){
//						@Override
//						public void onAutoFocus(boolean arg0, Camera arg1) {
//							//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//						}
//					});
//				}
//				
//				if(cameraSide == "BACK")
//				{
//					try{
//						//mCamera.takePicture(null, null, s3FixIloveS3);
//						mCamera.setErrorCallback(ec);
//						mCamera.takePicture(null, null, mPicture);
//						//Toast.makeText(getApplicationContext(),"Nice shot!",Field.SHOWTIME).show();
//						
//					}catch(Exception e){
//						//mCamera.takePicture(null, null, s3FixIloveS3);
//						Toast.makeText(getApplicationContext(),errorMessage,Field.SHOWTIME).show();
//						
//					}
//				}
			}
			
			else if(view.getId() == R.id.cumshot){
//				if(isSavable){
//					if(cameraSide == "BACK")
//						setSide("BACK");
//					else if(cameraSide == "FRONT")
//						setSide("FRONT");	
//				}
				
			}
			
//			else if(view.getId() == R.id.utilityButtonLayout){
//				try{
////					hideAct.ninjaMoves();
////					if(utilityLayout.isShown())
////						utilityLayout.setVisibility(LinearLayout.GONE);
////					else
////						utilityLayout.setVisibility(LinearLayout.VISIBLE);
//				}catch(Exception e){
//					
//				}
//				//else
//					//Toast.makeText(getApplicationContext(),"画像を保存してください",Field.SHOWTIME).show();
//				
//			}
		}
		catch(Exception e)
		{
			Log.i(TAG,"Error in here View = "+view.getId()+": Cause? I don't effing know -> "+e.getMessage());
			Toast.makeText(this,errorMessage,Field.SHOWTIME).show();
		}
	}
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if(keyCode == KeyEvent.KEYCODE_HOME)
//        {
//            relenquishTheSoul();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

//    @Override
//    public void onBackPressed() {
//        // do something on back.
//    	Log.i(TAG,"BACK is pressed");
//        return;
//    }
  
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
    	Log.i(TAG, "KeyCode = "+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	relenquishTheSoul();
            return true;
        }
//        else if(keyCode == KeyEvent.KEYCODE_HOME){
//        	relenquishTheSoul();
//            return true;
//        }

        return super.onKeyDown(keyCode, event);
    }

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    Log.i(TAG, "Destroying onDestroy : isConfigChanging = "+isConfigChanging);
	    releaseCamera();
//	    if(!isConfigChanging){
////	    	isConfigChanging	= false;
//	    	relenquishTheSoul();
//	    }
	    
	}
	
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//	    super.onConfigurationChanged(newConfig);
//
//	    // Checks the orientation of the screen
//	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//	    	isConfigChanging = true;
//	        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//	    	isConfigChanging = true;
//	        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//	    }
//	}
	
//	@Override
//    public void onConfigurationChanged(Configuration newConfig) 
//    {
//        super.onConfigurationChanged(newConfig);
//        isConfigChanging = true;
//        Log.i(TAG, "OnConfChange");
//    }
	
//	@Override
//	protected void onResume() {
//	    super.onResume();
//	    linkSTART();
//	    
//	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    Log.i(TAG, "Destroying onPause : isConfigChanging = "+isConfigChanging);
	    releaseCamera();
//	    if(!isConfigChanging){
////	    	isConfigChanging	= false;
//	    	relenquishTheSoul();
//	    }
	    
	}
	
//	 @Override
//	    public boolean onCreateOptionsMenu(Menu menu) {
//	    	getMenuInflater().inflate(R.menu.dual_cam, menu);
//	        return true;
//	    }
//	
//	public boolean onOptionsItemSelected(MenuItem item) {
//		 switch (item.getItemId()) {
//	        case R.id.savePath:
//	        	//startActivity(new Intent(this, About.class));
//	        	showFileChooser();
//	        return true;
//	        default:
//	        return super.onOptionsItemSelected(item);
//	        }
//	    }

	@Override
	public void onSaveInstanceState(Bundle toSave) {
	  super.onSaveInstanceState(toSave);
//	  isConfigChanging = true;
//	  Log.i(TAG, "from onSaveInstance isConfigChanging = "+isConfigChanging	);
	  toSave.putBoolean("isBackTaken", isBackTaken);
	  toSave.putBoolean("isFrontTaken", isFrontTaken);
	  toSave.putBoolean("isSavable", isSavable);
	  toSave.putBoolean("isSaved", isSaved);
	  toSave.putBoolean("isSavePathset", isSavePathset);
	  toSave.putBoolean("isTextEditable", isTextEditable);
	  toSave.putBoolean("isTextAdded", isTextAdded); 
	  toSave.putBoolean("isRetryable", isRetryable);
//	  toSave.putBoolean("isConfigChanging", isConfigChanging);
	  toSave.putInt("fontSize", fontSize);
	  
	  if(frontPic != null)
		  toSave.putParcelable("frontPic", frontPic);
	  
	  if(backPic != null)
		  toSave.putParcelable("backPic", backPic);
	  
	  if(cameraSide != null)
		  toSave.putString("cameraSide", cameraSide);
	  
	  if(fileName != null)
		  toSave.putString("fileName", fileName);
	  
	  if(textToShow != null)
		  toSave.putString("textToShow", textToShow);
	  
//	  if(mCamera != null)
//		  toSave.put
	  
	}    
    
    
    //Custom Methods
	public void addText(){
		
	}
	
	public CountDownTimer doDoubleTap(){
		CountDownTimer longClick = new CountDownTimer(10000, 500) 
		{
			
		     public void onTick(long millisUntilFinished) {
		    	 //Log.i(TAG," <------------------- millisUntilFinished : "+millisUntilFinished );
//		    	 if(performedAction == Field.ActionClickEnd)
//		    		 performedAction = Field.ActionLongClick;
		    	 if(performedAction == Field.ActionClick || performedAction == Field.ActionClickEnd)
		    	 	isDoubleTapAction = true;
		     }
	
		     public void onFinish() {
		    	 isDoubleTapAction = false;
		    	 tapCount = 0;
	
		     }
		  };
		
		return longClick;
	}
	
	public CountDownTimer cameraSetFocus(final String focusState, final Float touchX, final Float touchY, final View beingTouched){
		CountDownTimer camera = new CountDownTimer(500, 100) 
		{
			
		     public void onTick(long millisUntilFinished) {
//		    	 if(performedAction == Field.ActionClickEnd)
//		    		 performedAction = Field.ActionLongClick;
		    	 touchAction = Field.ActionFocusing;
		     }
	
		     
		     public void onFinish() {
		    	// setFocusMarker( "SHOOT", null, null, null);
		    	 if(mCamera != null){
						mCamera.autoFocus(new AutoFocusCallback(){
							@Override
							public void onAutoFocus(boolean arg0, Camera arg1) {
								//performedAction = Field.ActionLongClickEnd;
								//setFocusMarker("SHOOT",touchX, touchY,beingTouched);
//								if(performedAction != Field.ActionAutoFocus)
//									doubleTapTimer.start();	
								
//								setFocus("SHOOT", 
//										performedAction, 
//										touchX, 
//										touchY, 
//										beingTouched);
								Log.i(TAG, "cameraSetFocus : touchAction = "+touchAction);
								if(touchAction != Field.ActionAutoFocus){
									touchAction = Field.ActionFocusingEnd;
									setFocus("SHOOT", 
											performedAction, 
											touchX, 
											touchY, 
											beingTouched);
								}
								else if(touchAction == Field.ActionAutoFocus){
									touchAction = Field.ActionFocusingEnd;
									setFocus("SHOOT", 
											performedAction, 
											null, 
											null, 
											null);
								}
								
//								if(mCamera != null && isDoubleTapAction){
//									//doDoubleTap().cancel();
//									Log.i(TAG, "Is Double tap");
//									takeAShot();
//									isDoubleTapAction = false;
//									tapCount = 0;
//									
//								}
							}
						});
						
		    	 }
	
		     }
		  };
		  
		  return camera;
	}
	
	public CountDownTimer cameraFocus(){
		CountDownTimer longClick = new CountDownTimer(2000, 1000) 
		{
			
		     public void onTick(long millisUntilFinished) {
		    	 if(performedAction == Field.ActionClick)
		    		 performedAction = Field.ActionLongClick;
		     }
	
		     public void onFinish() {
		    	 doubleTapTimer.cancel();
				 isDoubleTapAction = false;
		    	 if(mCamera != null && performedAction == Field.ActionLongClick){
						mCamera.autoFocus(new AutoFocusCallback(){
							@Override
							public void onAutoFocus(boolean arg0, Camera arg1) {
								performedAction = Field.ActionLongClickEnd;
								//setFocusMarker("SHOOT",null, null,null);
								if(mCamera != null){
									takeAShot();
								}
							}
						});
						
		    	 }
	
		     }
		  };
		
		return longClick;
	}
	
	
	public void createAlert(String currentFunction,String thisside,String thismessage){
		final String title = currentFunction;
		final String side = thisside;
		final String message = thismessage;
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				DualCamActivity.this);
	
			// set title
			//alertDialogBuilder.setTitle(title);
	
			// set dialog message
			alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(yes,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						if(title == "retake"){
							isRetaking = true;
							if(isBackTaken && !isFrontTaken){
								ImageView buttonView = getPressedPreview("FRONT");
								buttonView.setBackgroundDrawable(getResources().getDrawable(R.drawable.whitebg));
								buttonView.setImageDrawable(getResources().getDrawable(R.drawable.previewfront));
							}
						 
							Log.i(TAG, "Initiating Retake :D");
							releaseCamera();
							setSide(side);
						}
					}
				  })
				.setNegativeButton(no,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						isRetaking = false;
					}
				});
	
			// create alert dialog
			AlertDialog alert = alertDialogBuilder.create();
	    	alert.show();
	    	
	}
	
	public void createAText(){
	    
	    // this is the method to create text on the picture
	    RelativeLayout rlv = (RelativeLayout)findViewById(R.id.buttonLayout);
	    
	    textFrameLayoutParams = (RelativeLayout.LayoutParams)createTextFrameLayout.getLayoutParams();
	    
	    //layoutParams.addRule(RelativeLayout.ABOVE);  
	    addedText = new TextView(getApplicationContext()); 
	    //addedText.setTextSize(50);         
	    //addedText.setGravity(Gravity.END);
	    //addedText.setGravity(Gravity.BOTTOM);   
	    //addedText.setGravity(Gravity.RIGHT); 
	    addedText.setText("");     
	    addedText.setTextSize(40);
	    addedText.setTextColor(Color.RED);   
	    addedText.setGravity(Gravity.TOP);   
	    addedText.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		    	switch(event.getActionMasked())
                {
                        case MotionEvent.ACTION_DOWN:
                        		addedTextX = (int)event.getX();
                        		addedTextY = (int)event.getY();
                                //selected_item = v;
                                break;
                        default:
                                break;
                }
                  
                return false;
		    }
		});
	    
	    createTextFrameLayout.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        switch(event.getActionMasked())
                {
                	case MotionEvent.ACTION_MOVE:
                		int x = (int)event.getX() - addedTextX;
                		int y = (int)event.getY() - addedTextY;

                		int w = getWindowManager().getDefaultDisplay().getWidth() - 100;
                		int h = getWindowManager().getDefaultDisplay().getHeight() - 100;
                			
                		if(x > w)
                		x = w;
                		if(y > h)
                		y = h;
                        
                	FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                 new FrameLayout.MarginLayoutParams(
                                		 FrameLayout.LayoutParams.WRAP_CONTENT,
                                		 FrameLayout.LayoutParams.WRAP_CONTENT));

                		lp.setMargins(x, y, 0, 0);
                		lp.gravity = Gravity.TOP;
                		addedText.setLayoutParams(lp);
                                break;
                                
                        default:
                                break;
                }
                return true;
		    }
		});
	    
	    //layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, 1);
	    //layoutParams.setMargins(screenWidth - (textToShow.length() * fontSize),0, 0,0);
	    //layoutParams.setMargins(0, (screenHeight - (saveButton.getHeight())), 0,0);
	    //layoutParams.setMargins(screenWidth - (textToShow.length() * fontSize), (screenHeight - (fontSize *2)), 0,0);

	    //layoutParams.setMargins(screenWidth - (80),  (screenHeight - (saveButton.getHeight() * 2)), 0,0);
	    textFrameLayoutParams.setMargins(10,10, 0,0);
	    textFrameLayoutParams.addRule(RelativeLayout.ABOVE);  
	    addedText.setLayoutParams(textFrameLayoutParams);
	    
	    isTextAdded = true;
	    isTextEditable = true;
		setButtons(isSaved,isSavable,isTextEditable,isRetryable);
	    createTextFrameLayout.addView(addedText, textFrameLayoutParams); 
	    createTextFrameLayout.bringToFront();
	    //Log.i(TAG, ":D = "addedText.isShown());
	    //rlv.bringToFront();  
	  }
	
	public void customAlertdialog(){
	    
	      final AlertDialog.Builder alert = new AlertDialog.Builder(this); 
	      final int progressBarCompensation = 30;
	      LinearLayout linear=new LinearLayout(this); 

	      linear.setOrientation(1); 
	      createAText();
	      //utilityLayout.setVisibility(LinearLayout.GONE);
	      
	      //The EditText
	      final EditText toBeText = new EditText(this);
	      toBeText.setHint("Type text here...");
	      toBeText.addTextChangedListener(
	    		  new TextWatcher(){
	          public void afterTextChanged(Editable s) {

	          }
	          public void beforeTextChanged(CharSequence s, int start, int count, int after){
	        	  
	        	  
	          }
	          public void onTextChanged(CharSequence s, int start, int before, int count){
	        	  charCount++;
	        	  Log.i(TAG, "charCount = "+charCount);
	        	  addedText.setText(toBeText.getText().toString());
	        	  //addedText.setText(addedText.getText().toString());
	        	  //tv.setText(String.valueOf(i) + " / " + String.valueOf(charCounts));
	        	  
	          }
	      }); 
	      
	      //The TextView
	      final TextView textfontSize = new TextView(this); 
	      textfontSize.setText("Font Size = " + (fontSize + 40 )); 
	      textfontSize.setPadding(10, 10, 10, 10); 
	        
	      //The SickBar
	      SeekBar seek=new SeekBar(this); 
	      //seek.setProgress(50);
	      
	      seek.setProgress(defaultTextSize - progressBarCompensation);
	      seek.setMax(maximumTextSize - progressBarCompensation);
	      //seek.setProgress(fontSize);
	      
	      //The Color Pallete
	      Button bt = new Button(this);
	      bt.setText("Select a Font Color");
	      bt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	      bt.setOnClickListener(new OnClickListener(){
	    	   public void onClick(View v) {
	    		   
	    		   //addedText
	    		   
	    		   	//Changes by Aid
	    			initialColor = PreferenceManager.getDefaultSharedPreferences(DualCamActivity.this).getInt(COLOR_PREFERENCE_KEY,Color.WHITE);
	    			colorPickerDialog=new ColorPickerDialog(
	    					DualCamActivity.this, 
	    					DualCamActivity.this, 
	    					initialColor,
	    					addedText);
	    			//Changes by Aid
	    		    colorPickerDialog.show();
	    	   }
	      });
	      
	      linear.addView(toBeText); 
	      linear.addView(seek); 
	      linear.addView(textfontSize); 
	      linear.addView(bt); 
	      
	      alert.setView(linear); 
	      

	      seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	            textfontSize.setText("Font Size = " + fontSize ); 
	            fontSize = progress + progressBarCompensation;
	            addedText.setTextSize(fontSize);
	          }

	      public void onStartTrackingTouch(SeekBar arg0) {

	        
	      }

	      public void onStopTrackingTouch(SeekBar seekBar) {

	    	  
	      }
	      });

	      alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() 
	      { 
	          public void onClick(DialogInterface dialog,int id)  
	          {
	        	  
	        	
	        	  
	            //textToShow = toBeText.getText().toString();
	            //createTextFrameLayout.removeAllViews();
	            //textFrameLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
	            //	textFrameLayoutParams.addRule(verb, anchor)
	            //addedText.
	            //textFrameLayoutParams.setMargins(0, 0, 0, 0);
	            //addedText.setGravity(Gravity.RIGHT);
	            //textFrameLayoutParams.setMargins(0,  (screenHeight - (saveButton.getHeight())), 5,0);
	            //addedText.setLayoutParams(textFrameLayoutParams);
	            //createTextFrameLayout.addView(addedText, textFrameLayoutParams); 
	    	    //createTextFrameLayout.bringToFront();
	            //utilityLayout.setVisibility(LinearLayout.VISIBLE);
	            //createAText();
	          }
	      }); 

	      alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener()  
	      { 
	          public void onClick(DialogInterface dialog,int id)  
	          { 
	             // Toast.makeText(getApplicationContext(), "Cancel Pressed",Toast.LENGTH_LONG).show(); 
	        	    createTextFrameLayout.removeAllViews();
	        	  	isTextAdded = false;
	      	    	isTextEditable = true;
	      	    	setButtons(isSaved,isSavable,isTextEditable,isRetryable);

		            //utilityLayout.setVisibility(LinearLayout.VISIBLE);
	              return; 
	          } 
	      }); 
	      alert.setOnDismissListener(
	    		  new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				//fontSize = defaultTextSize - progressBarCompensation;

			}
		});
	      alert.show();   
	    
	  }
	
//	public void customAlertdialog(){
//		///Code ni Aid here :D
//		
//	}
	
	public void doZoom(Float firstDistance, Float secondDistance ){
		if(firstDistance < secondDistance){
			//Do zoom in
			Log.i(TAG, "Zoom in");
//			if(isSmoothZoomSupported){
//				
//			}
//			else 
				if(isZoomSupported){
				if(currentCameraZoom < maxCameraZoom){
					
					int zoomLevel = currentCameraZoom + 1;
					param.setZoom(zoomLevel);
					mCamera.setParameters(param);
					currentCameraZoom=param.getZoom();
				}
			}
			else{
				//Zoom not supported
				//Toast.makeText(getApplicationContext(),"Sorry, your phone don't have zoom features",Field.SHOWTIME).show();
			}
				
		}
		else if(firstDistance > secondDistance){
			//Do zoom out
			Log.i(TAG, "Zoom out");
//			if(isSmoothZoomSupported){
//				
//			}
//			else 
				if(isZoomSupported){
				if(currentCameraZoom > 0){
					int zoomLevel = currentCameraZoom - 1;
					param.setZoom(zoomLevel);
					mCamera.setParameters(param);
					currentCameraZoom=param.getZoom();
				}
			}
			else{
				//Zoom not supported
			}
		}
	}
    
    public ErrorCallback ec = new ErrorCallback(){

		@Override
		public void onError(int data, Camera camera) {
			Log.i(TAG,"ErrorCallback received");
		}
		
	};
    
	public PictureCallback getPic = new PictureCallback() {
			
		    @Override
		    public void onPictureTaken(byte[] data, Camera camera) {
		    	
		    	try {
		    		isRetaking  = false;
		    		isRetryable = true;
		    		setButtons(isSaved,isSavable,isTextEditable,isRetryable);
		    		ImageView buttonView = getPressedPreview(cameraSide);
		    		Matrix matrix = new Matrix(); 
		    		int width = 0;
			    	int height = 0;
			    	int extraWidth = 0 ;
		           	int extraHeight = 0;
		           	int marginalWidth = 0;
		           	int marginalHeight = 0;
		    		
	//	         	if(tempPic != null){
	//	           		tempPic.recycle();
	//	           		tempPic = null;
	//	           	}
		    		
			    	
			    	
			    	
			    	Log.i(TAG,"Pic taken");
			            if(cameraSide == "BACK")
			            {
			            	Log.i(TAG,"Side = "+cameraSide);
			            	setRetake(cameraSide);
			            	matrix.postRotate(result); 
			            	
			            	
				    		options = new BitmapFactory.Options();
				 	  		options.inSampleSize = 1;
				 	  		options.inJustDecodeBounds = true;
				 	  		
				 	  		// Determine how much to scale down the image
				 	  	    //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
				 	  	    
				 	  		Bitmap temp = BitmapFactory.decodeByteArray(data, 0,  data.length);
				 			int xW = temp.getWidth();
				 			int xH = temp.getHeight();
				 	  		// Determine how much to scale down the image
				 	  	    //int scaleFactor = Math.min(xW/screenHeight, xH/screenWidth);
				 			int scaleFactor = Math.max(xW/screenHeight, xH/screenWidth);
				 	  		// Calculate inSampleSize
				 		    //options.inSampleSize = bitmapResizer.calculateInSampleSize(options, shortWidth, shortHeight);
				 		    options.inSampleSize = scaleFactor;
				 		    // Decode bitmap with inSampleSize set
				 		    options.inJustDecodeBounds = false;
				 		    tempPic = BitmapFactory.decodeByteArray(data, 0,  data.length,options);
				 		    width = tempPic.getWidth();
				           	height = tempPic.getHeight();
				 		    
				           	Log.i(TAG,"Before SShot");
				           	Log.i(TAG,"*******************   TADAA!!   ***************************");
				           	Log.i(TAG,"xW = "+xW+": xH = "+xH);
				           	Log.i(TAG,"Width = "+tempPic.getWidth()+": Height = "+tempPic.getHeight());
				           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
				           	Log.i(TAG,"scaleFactor = "+scaleFactor);
				           	
				           	Log.i(TAG,"*******************   TADAA!!   ***************************");
				           	if(width > screenHeight || height > screenWidth){
				           		if(width > 1280 || height > 1280){
				           			tempPic = Bitmap.createScaledBitmap(tempPic, Math.round(width/2), Math.round( height /2),true);
				           		}
				           		tempPic = Bitmap.createBitmap(tempPic, 0,0,tempPic.getWidth(), tempPic.getHeight(), matrix, true);
				           		width = tempPic.getWidth();
					           	height = tempPic.getHeight();
					           	extraWidth = width - screenWidth;
					           	extraHeight = height - screenHeight;
					           	marginalWidth = Math.round(extraWidth/2);
					           	marginalHeight = Math.round(extraHeight/2);
					           	if(marginalHeight < 0)
					           		marginalHeight = 0;
					           	if(marginalWidth < 0)
					           		marginalWidth = 0;
					           	if(extraWidth < 0)
					           		extraWidth = 0;
					           	if(extraHeight < 0)
					           		extraHeight = 0;
					           	
					           	Log.i(TAG,"Width = "+width+": Height = "+height);
					           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
					           	Log.i(TAG,"marginalWidth = "+marginalWidth+": marginalHeight = "+marginalHeight);
					           	
					           	if(orientationScreen == "PORTRAIT"){
					           		tempPic = Bitmap.createBitmap(tempPic,marginalWidth,marginalHeight,width - extraWidth, height - marginalHeight);
					           		//tempPic = Bitmap.createBitmap(tempPic,0,0,width, height);
					           	}
					        }
				           	else{
				           		tempPic = Bitmap.createBitmap(tempPic, 0,0,tempPic.getWidth(), tempPic.getHeight(), matrix, true);
				           		width = tempPic.getWidth();
					           	height = tempPic.getHeight();
					           	Log.i(TAG,"Width = "+width+": Height = "+height);
					           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
				           	}
				           	
				           	width = tempPic.getWidth();
				           	height = tempPic.getHeight();
				            //tempPic = Bitmap.createBitmap(tempPic, 0,0,width, Math.round(height/2));
				           	
				           	if(orientationScreen == "PORTRAIT"){
				           		//Portrait
				           		tempPic = Bitmap.createBitmap(tempPic, 0,0,width,height - Math.round(height/3));
				           		
				           	}
				           	else if(orientationScreen == "LANDSCAPE"){
				           		//Landscape
				           		tempPic = Bitmap.createBitmap(tempPic, 0,0,width - Math.round(width/3),height);
				           		
				           	}
				           	
				 		    settoBackground(buttonView,tempPic);
				 		    backPic = tempPic;
			            	mCamera.stopPreview();
			            	releaseCamera();
			            	previewImage.setVisibility(ImageView.GONE);
			            	isBackTaken = true;
					        
					        if(!isFrontTaken )
					        {
					        	setSide("FRONT");
					        	//setUntake("BACK");
					        }
			            	
			            	
			            }
			            else
			            {
			            	Log.i(TAG,"Side = "+cameraSide);
			            	setRetake(cameraSide);
			            	matrix.postRotate(result); 
				 		    matrix.preScale(-1, 1);
				           	
			            	options = new BitmapFactory.Options();
				 	  		options.inSampleSize = 1;
				 	  		options.inJustDecodeBounds = true;
				 	  		
				 	  		// Determine how much to scale down the image
				 	  	    //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
				 	  	  
				 	  		Bitmap temp = BitmapFactory.decodeByteArray(data, 0,  data.length);
				 			int xW = temp.getWidth();
				 			int xH = temp.getHeight();
				 	  		// Determine how much to scale down the image
				 	  	    int scaleFactor = Math.min(xW/screenHeight, xH/screenWidth);
				 	  		// Calculate inSampleSize
				 		    //options.inSampleSize = bitmapResizer.calculateInSampleSize(options, shortWidth, shortHeight);
				 		    options.inSampleSize = scaleFactor;
				 		    // Decode bitmap with inSampleSize set
				 		    options.inJustDecodeBounds = false;
					    		
				 		    tempPic = BitmapFactory.decodeByteArray(data, 0,  data.length,options);
				 		    width = tempPic.getWidth();
				           	height = tempPic.getHeight();
				 		    
				           	Log.i(TAG,"Before SShot");
				           	Log.i(TAG,"Width = "+tempPic.getWidth()+": Height = "+tempPic.getHeight());
				           	
				           	if(width > screenHeight || height > screenWidth){
				           		if(width > 1280 || height > 1280){
				           			tempPic = Bitmap.createScaledBitmap(tempPic, Math.round(width/2), Math.round( height /2),true);
				           		}
				           		tempPic = Bitmap.createBitmap(tempPic, 0,0,tempPic.getWidth(), tempPic.getHeight(), matrix, true);
				           		width = tempPic.getWidth();
					           	height = tempPic.getHeight();
					           	extraWidth = width - screenWidth;
					           	extraHeight = height - screenHeight;
					           	marginalWidth = Math.round(extraWidth/2);
					           	marginalHeight = Math.round(extraHeight/2);
					           	if(marginalHeight < 0)
					           		marginalHeight = 0;
					           	if(marginalWidth < 0)
					           		marginalWidth = 0;
					           	if(extraWidth < 0)
					           		extraWidth = 0;
					           	if(extraHeight < 0)
					           		extraHeight = 0;
					           	
					           	Log.i(TAG,"Width = "+width+": Height = "+height);
					           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
					           	Log.i(TAG,"marginalWidth = "+marginalWidth+": marginalHeight = "+marginalHeight);
					           	Log.i(TAG,"Resizing~ ching ching!");
					           	
					           	if(orientationScreen == "PORTRAIT"){
					           		tempPic = Bitmap.createBitmap(tempPic,marginalWidth,marginalHeight,width - extraWidth, height - marginalHeight);
					           	}
					        }
				           	else{
				           		tempPic = Bitmap.createBitmap(tempPic, 0,0,tempPic.getWidth(), tempPic.getHeight(), matrix, true);
				           		width = tempPic.getWidth();
					           	height = tempPic.getHeight();
					           	Log.i(TAG,"Width = "+width+": Height = "+height);
					           	Log.i(TAG,"screenWidth = "+screenWidth+": screenHeight = "+screenHeight);
					           	Log.i(TAG,"Unresized booya!");
				           	}
				           	width = tempPic.getWidth();
				           	height = tempPic.getHeight();
				           	boolean b = tempPic.isMutable();
				           	Log.i(TAG, "The reason = "+b);
				           	Log.i(TAG,"Flag 1");
				            //tempPic = Bitmap.createBitmap(tempPic, 0,Math.round(height/2),width, height/2);
				           	
				        	if(orientationScreen == "PORTRAIT"){
				           		//Portrait
				        		tempPic = Bitmap.createBitmap(tempPic, 0,Math.round(height/3),width, height - Math.round(height/3));
				        	}
				        	else if(orientationScreen == "LANDSCAPE"){
				        		//Landscape
				        		tempPic = Bitmap.createBitmap(tempPic, Math.round(width/3),0,width  - Math.round(width/3), height);
				        		
				        	}
				        	
				           	Log.i(TAG,"Flag 2");
				            settoBackground(buttonView,tempPic);
				            frontPic = tempPic;
				           	Log.i(TAG,"Flag 3");
				 		    mCamera.stopPreview();
			 	            releaseCamera();
			 	            isFrontTaken = true;
	
	
			            }
			            //FileOutputStream fos = new FileOutputStream(pictureFile);
			            //fos.write(data);
			            //fos.close();
			            if(isBackTaken && isFrontTaken){
			            	isRetryable = true;
			            	isSaved = false;
				    		isSavable = true;
				    		isTextEditable = true;
				    		//textButton.setImageResource(R.drawable.text1);
				    		//saveButton.setImageResource(R.drawable.save1);
				    		setButtons(isSaved,isSavable,isTextEditable,isRetryable);
//				    		if(!isTextAdded)
//				    			setEditText();
				    		
				    	}
			        }catch (Exception e) {
			        	Log.i(TAG,"not isSaved");
			        	Log.e(TAG,"Error accessing file: " + e.getMessage());
			        	Toast.makeText(getApplicationContext(),errorMessage,Field.SHOWTIME).show();
			        	//linkSTART();
			        	
			        }
		    }
	};
	
	public ImageView getPressedPreview(String cameraSide){
		ImageView buttonView = null;
		
		if(cameraSide == "BACK")
			buttonView = backPreview;
        if(cameraSide == "FRONT")
        	buttonView = frontPreview;
        
		return buttonView;
	}
	
	public void linkSTART(){
		finish();
		Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
	
	public void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
	
	public void relenquishTheSoul(){
		releaseCamera();              // release the camera immediately on pause event
		
        options = null;
        if(tempPic != null)
        	tempPic.recycle();
        finish();
        System.exit(0);
	}
	
	 public void retakeImage(String thisside){
		 
		  cameraSide = thisside;
		  String message = retakeMessage;
		  String title = "retake";
		  createAlert(title,cameraSide,message);
	  }
	 
	 public void saveImage(Bitmap bmp){
			try {
					
//				   MediaStore.Images.Media.insertImage(getContentResolver(), bmp,"","");
				   //Log.d(TAG,"the filename = "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
				   //if(!isSavePathset)
				   fileName = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString();
				   //Log.d(TAG,"The utility = "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			       FileOutputStream out = new FileOutputStream(mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE));
			       //Log.d(TAG,"Before saving");
			       bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			       //Log.d(TAG,"After saving");
			       mediaUtility.updateMedia(TAG,"file://" +mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			       //Log.d(TAG,"file://" +mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			       out.flush();
			       out.close();
			       //Log.d(TAG,"Saved to "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			       Toast.makeText(getApplicationContext(),"写真の保存が完了しました。",Field.SHOWTIME).show();
			       isSaved = true;
			       //shareButton.setImageResource(R.drawable.share1);
			       setButtons(isSaved,isSavable,isTextEditable,isRetryable);
			  
			} catch (Exception e) {
			       e.printStackTrace();
			       Log.d(TAG,"Saving failed cause = "+ e.getCause() );
			       Toast.makeText(getApplicationContext(),errorMessage,Field.SHOWTIME).show();
					
			}
		}

	public void seePreview(String cameraSide){
		try{

        	previewImage.setVisibility(ImageView.GONE);
			mainPreview.setVisibility(FrameLayout.VISIBLE);
			isSaved = false;
			isSavable = false;
			isTextEditable = false;
			isRetryable = false;
			setButtons(isSaved,isSavable,isTextEditable,isRetryable);
			createTextFrameLayout.setVisibility(FrameLayout.VISIBLE);
			//saveButton.setImageResource(R.drawable.save2);
			releaseCamera();
			setUntake(cameraSide);
			ImageView buttonView = getPressedPreview(cameraSide);
			cameraUtility = new CameraUtility(getApplicationContext());
			Log.i(TAG, "1");
			
			//Normal
			//mCamera = cameraUtility.getCameraInstance(cameraSide,screenHeight,screenWidth,orientationScreen);
			mCamera = cameraUtility.getCameraInstance(cameraSide,screenHeight/2,screenWidth,orientationScreen);
			
			//Instantiate camera Zoom
			param = mCamera.getParameters();
			isZoomSupported		 = param.isZoomSupported();
			isSmoothZoomSupported= param.isSmoothZoomSupported();
			if(isZoomSupported || isSmoothZoomSupported){
				maxCameraZoom = param.getMaxZoom();
				currentCameraZoom=param.getZoom();
			}
			Log.i(TAG, "isZS = "+ isZoomSupported +" : isSZS = "+isSmoothZoomSupported);
			

			
			setCameraDisplayOrientation(this,cameraUtility.findCamera(cameraSide),mCamera);
			Log.i(TAG, "b");
			cameraPreview = new CameraPreview(getApplicationContext(), mCamera);
			mainPreview.removeAllViews();
			
			
			android.widget.RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams)mainPreview.getLayoutParams();
			
			//LayoutParams grav = (LayoutParams) mainPreview.getLayoutParams();
			//int gr = grav.getClass()
			RelativeLayout rl = (RelativeLayout)findViewById(R.id.addCamPreview);
			//android.widget.RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) rl.getLayoutParams();
			
			
			if(orientationScreen == "PORTRAIT"){
				//Log.i(TAG,"PASOK DITO!!!");
				if(cameraSide == "BACK"){
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
					
					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth,(screenHeight*3)/4);
					if(sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(0,0, 0, 0);
					else
						layoutParams.setMargins(0,0, 0, 0);
					cameraPreview.setLayoutParams(lp);
				}
				else{
					
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth,(screenHeight*3)/4);
					if(sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(0,screenHeight/4, 0, 0);
					else
						layoutParams.setMargins(0,screenHeight/4, 0, 0);
					cameraPreview.setLayoutParams(lp);
				}

				
			}
			else if(orientationScreen == "LANDSCAPE"){
				if(cameraSide == "BACK"){
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
					
					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((screenWidth*3)/4,screenHeight);
					if(sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(0,0, 0, 0);
					else
						layoutParams.setMargins(0,0, 0, 0);
					cameraPreview.setLayoutParams(lp);
				}
				else{
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
					
					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((screenWidth*3)/4,screenHeight);
					//lp.setMargins(screenWidth/4,0, 0, 0);
					if(sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(screenWidth/4,0, 0, 0);
					else
						layoutParams.setMargins(screenWidth/4,0, 0, 0);
					cameraPreview.setLayoutParams(lp);
				}
			}
			
			mainPreview.setLayoutParams(layoutParams);
			mainPreview.addView(cameraPreview);
			Log.i(TAG, "2");
	
			buttonView.setBackgroundDrawable(null);
			buttonView.setImageBitmap(null);
			//mCamera.getParameters().
			hasCameraFocus = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
			//touchAction = Field.ActionAutoFocus;
			Log.i(TAG, "seePreview : touchAction = "+touchAction);
			if(touchAction == Field.ActionNothing){
				touchAction = Field.ActionAutoFocus;
				setFocus("FOCUS", performedAction, null, null, null);
				
			}
			
				
			
			//cameraOnFocus().start();
			//setFocusMarker("FOCUS",null,null,null);		
		}catch(Exception e){
			Log.e(TAG,"Di ko na alam to wtf ftw");
			Log.e(TAG,"e = "+e.getCause());
			
			//Toast.makeText(getApplicationContext(),"OOPS!! Error = "+e.getMessage(),Field.SHOWTIME).show();
			Toast.makeText(getApplicationContext(),errorMessage,Field.SHOWTIME).show();
        	//linkSTART();
		}
	}
	
	public void setButtons(boolean shareState,boolean saveState,boolean textState,boolean retryState){
		
		if(shareState)
			shareButton.setImageResource(R.drawable.share1);
		else
			shareButton.setImageResource(R.drawable.share2);
		
		if(saveState)
			saveButton.setImageResource(R.drawable.save1);
		else
			saveButton.setImageResource(R.drawable.save2);
		
		if(textState)
			textButton.setImageResource(R.drawable.text1);
		else
			textButton.setImageResource(R.drawable.text2);
		
		if(retryState)
			retryButton.setImageResource(R.drawable.retry1);
		else
			retryButton.setImageResource(R.drawable.retry2);
	}
	
	public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
		 Parameters params;
		 int width = 0;
		 int height = 0;
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
	     
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	    
	     Log.i(TAG,"Degrees = "+degrees);
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     
	     width = cameraUtility.getCamWidth();
    	 height = cameraUtility.getCamHeight();

	     Log.i(TAG,"width "+width);
	     Log.i(TAG,"RESULT = "+result);
	     camera.setDisplayOrientation(result);
	 }
	
	public void setEditText(){
		
		isTextAdded = false;
		isTextEditable = true;
		setButtons(isSaved,isSavable,isTextEditable,isRetryable);
		//textButton.setImageResource(R.drawable.text2);
		//Do the Text
		AlertDialog.Builder alertDialogBuilderCreateText = new AlertDialog.Builder(DualCamActivity.this);
          
          // set title
          alertDialogBuilderCreateText.setTitle("Create Text");
      
          // set dialog message
          alertDialogBuilderCreateText
            .setMessage("Add some personalized message?")
            .setCancelable(false)
            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog,int id) {
            	createTextFrameLayout.removeAllViews();
                customAlertdialog();
              }
              })
            .setNegativeButton("No",new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog,int id) {
                
            	  	isTextAdded = false;
	      	    	isTextEditable = true;
	      	    	setButtons(isSaved,isSavable,isTextEditable,isRetryable);
              }
            });
      
          
          // create alert dialog
          AlertDialog alert = alertDialogBuilderCreateText.create();
            alert.show();
	}
	
	public void setFocus(final String focusCommand, final Integer action, final Float coordX, final Float coordY, final View beingFocused){
		//performedAction = action;
		cameraFocusTimer = cameraSetFocus(focusCommand, coordX, coordY, beingFocused);
		Log.i(TAG,"performedAction = "+performedAction+" : focusCommand = " + focusCommand);
		
		//HAS focus
		if(focusCommand == "FOCUS" && hasCameraFocus){
			isCameraFocused = false;
			setFocusMarker(focusCommand, coordX, coordY, beingFocused);
			cameraFocusTimer.start();
		}
		
		//NO focus
		else if(focusCommand == "FOCUS" && !hasCameraFocus){
			isCameraFocused = true;
			setFocusMarker("SHOOT", coordX, coordY, beingFocused);
		}
		
		else if(focusCommand == "SHOOT"){
			isCameraFocused = true;
			setFocusMarker(focusCommand, coordX, coordY, beingFocused);
//			if(mCamera != null)
//				takeAShot();
		}
		
		else if(focusCommand == "UNFOCUS"){
			setFocusMarker(focusCommand, coordX, coordY, beingFocused);
		}
		
		
		
	}
	
	public void setFocusMarker(String focusState, Float touchX, Float touchY, View beingTouched){
		Log.i(TAG, "Touch Coordinates X = "+touchX + " : Touch Coordinates Y = "+touchY+" : focusState = "+focusState);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(80,80);
		lp.gravity = Gravity.TOP;
		final int gravityLP = lp.gravity;
		Log.i(TAG, "The Gravity = "+gravityLP);
		if(focusState == "FOCUS"){
			focusMarker = new ImageView(getApplicationContext());
			focusMarker.setImageDrawable(getResources().getDrawable(R.drawable.focusmark1));
			
			//FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(80,80);
			//Log.i(TAG, "The Size = "+focusMarker.getWidth());
			if(touchX != null && touchY != null){
				if(cameraSide == "BACK" && beingTouched.getId() == R.id.cumPreviewBack){
					lp.setMargins((int) Math.abs(touchX - 40) , (int) Math.abs(touchY - 40) , 0,0);
					//Log.i(TAG, "It went here"+(int) Math.abs(touchX - 40));
						//lp.setMargins((screenWidth - 80) / 4 , (screenHeight - 80) / 2, 0,0);
					focusMarker.setLayoutParams(lp);
					mainPreview.addView(focusMarker);
				}
				else if(cameraSide == "FRONT" && beingTouched.getId() == R.id.cumPreviewFront){
					if(orientationScreen == "PORTRAIT"){
						if(sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN){
							lp.setMargins((int) Math.abs(touchX - 40) , (int) Math.abs(touchY - 40) + (screenHeight /2), 0,0);							
						}
						else{
							//lp.setMargins((int) Math.abs(touchX - 40) , (int) Math.abs(touchY ) + 150, 0,0);
							lp.setMargins((int) Math.abs(touchX - 40) , (int) Math.abs(touchY - 40 ) + frontPreview.getHeight() / 2 , 0,0);
							
						}
						
					}
					else if(orientationScreen == "LANDSCAPE"){
						if(sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN){
							//lp.setMargins((int) Math.abs(touchX - 40) , (int) Math.abs(touchY - 40) + (screenHeight /2), 0,0);							
							lp.setMargins((int) Math.abs(touchX - 40)  + (screenWidth /2), (int) Math.abs(touchY - 40), 0,0);
							
						}
						else{
							//lp.setMargins((int) Math.abs(touchX - 40) , (int) Math.abs(touchY ) + 150, 0,0);
							//lp.setMargins((int) Math.abs(touchX - 40) , (int) Math.abs(touchY - 40 ) + frontPreview.getHeight() / 2 , 0,0);
							lp.setMargins((int) Math.abs(touchX - 40)  + (frontPreview.getWidth() /2), (int) Math.abs(touchY - 40), 0,0);
							
						}
					}
						
					focusMarker.setLayoutParams(lp);
					mainPreview.addView(focusMarker);
				}
				
			}
			else if(touchX == null && touchY == null && beingTouched != null){
				
				if(cameraSide == "BACK" && beingTouched.getId() == R.id.cumPreviewBack){
					if(orientationScreen == "PORTRAIT")
						lp.setMargins((screenWidth - 80) / 2 , (screenHeight - 80) / 4, 0,0);
					else if(orientationScreen == "LANDSCAPE")
						lp.setMargins((screenWidth - 80) / 4 , (screenHeight - 80) / 2, 0,0);
					
					focusMarker.setLayoutParams(lp);
					mainPreview.addView(focusMarker);
				}
				else if(cameraSide == "FRONT" && beingTouched.getId() == R.id.cumPreviewFront){
					if(orientationScreen == "PORTRAIT")
						lp.setMargins((screenWidth - 80) / 2 	  , ((screenHeight - 80) * 3 )/4, 0,0);
					else if(orientationScreen == "LANDSCAPE")
						lp.setMargins(((screenWidth - 80) * 3 )/4 , (screenHeight - 80) / 2, 0,0);
					
					focusMarker.setLayoutParams(lp);
					mainPreview.addView(focusMarker);
				}
				
			}
			//else if(touchX == null && touchY == null && beingTouched == null){
			else {
				if(cameraSide == "BACK"){
					if(orientationScreen == "PORTRAIT")
						lp.setMargins((screenWidth - 80) / 2 , (screenHeight - 80) / 4, 0,0);
					else if(orientationScreen == "LANDSCAPE")
						lp.setMargins((screenWidth - 80) / 4 , (screenHeight - 80) / 2, 0,0);
					
					focusMarker.setLayoutParams(lp);
					mainPreview.addView(focusMarker);
				}
				else if(cameraSide == "FRONT"){
					if(orientationScreen == "PORTRAIT"){
						//lp.gravity = -1;
						//lp.setMargins((screenWidth - 80) / 2 	  , (frontPreview.getHeight() /2) + frontPreview.getHeight(), 0,0);
						//Log.i(TAG, "screenWidth = "+screenWidth+" : screenHeight = "+screenHeight+" : frontPreview.getHeight() = "+frontPreview.getHeight()+" : backPreview.getHeight() = "+backPreview.getHeight()+" : mainPreview = "+mainPreview.getHeight()+" : ((screenHeight - 80) * 3 )/4 = "+((screenHeight - 80) * 3 )/4);
						lp.setMargins((screenWidth - 80) / 2,(int) ((screenHeight - 80) * 3 )/4, 0,0);
						//lp.setMargins((screenWidth - 80) / 2 	  ,frontPreview.getHeight(), 0,0);
					}	
					else if(orientationScreen == "LANDSCAPE")
						lp.setMargins(((screenWidth - 80) * 3 )/4 , (screenHeight - 80) / 2, 0,0);
					
					focusMarker.setLayoutParams(lp);
//					mainPreview.addView(focusMarker);
				}
				
			}
			
		}
		else if(focusState == "UNFOCUS"){
			if(focusMarker != null)
				mainPreview.removeView(focusMarker);
	    		//focusMarker.setImageDrawable(getResources().getDrawable(R.drawable.focusmark2));
		}
		else if(focusState == "SHOOT"){
			if(focusMarker != null)
			{
				focusMarker.setImageDrawable(getResources().getDrawable(R.drawable.focusmark2));
				
				//Callback for capture button 
				if(touchX == null && touchY == null && performedAction != Field.ActionLongClickEnd){
					if(cameraSide == "BACK"){
						if(orientationScreen == "PORTRAIT")
							lp.setMargins((screenWidth - 80) / 2 , (screenHeight - 80) / 4, 0,0);
						else if(orientationScreen == "LANDSCAPE")
							lp.setMargins((screenWidth - 80) / 4 , (screenHeight - 80) / 2, 0,0);
						
						focusMarker.setLayoutParams(lp);
					}
					else if(cameraSide == "FRONT"){
						if(orientationScreen == "PORTRAIT")
							lp.setMargins((screenWidth - 80) / 2 	  , ((screenHeight - 80) * 3 )/4, 0,0);
						else if(orientationScreen == "LANDSCAPE")
							lp.setMargins(((screenWidth - 80) * 3 )/4 , (screenHeight - 80) / 2, 0,0);
						
						focusMarker.setLayoutParams(lp);
					}
					
				}
				
			}
				
		}
			
		
	}
	
	
	public void setHide(){
		
	}
	

	public void setRetake(String cameraSide){
		  
		  
		  if(cameraSide == "BACK"){
			  isBackTaken = true;
		  }
		  else if(cameraSide == "FRONT"){
			  isFrontTaken = true;
		  }
	  }
	
	
	public void setSide(String thisside){
		  touchAction = Field.ActionNothing;
		  cameraSide = thisside;
		  seePreview(cameraSide);
	 }
	
	public void settoBackground(View view, Bitmap bitmap){
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
    	   view.setBackgroundDrawable(bd);
        } else {
    	   view.setBackground(bd);
        }
	}
	
	public class setTouchMode implements View.OnTouchListener
	{

		@Override
		public boolean onTouch(final View view, final MotionEvent event) {
			//Toast.makeText(getApplicationContext(), "Number of points = "+event.getPointerCount(), Field.SHOWTIME).show();
			longClickTimer = cameraFocus();
			touchCount = event.getPointerCount();
			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_POINTER_DOWN:
					topTapCount = 0;
					bottomTapCount = 0;
					Log.i(TAG, "MotionEvent = ACTION_POINTER_DOWN");
					performedAction = Field.ActionZoom;
					touchAction = performedAction;
					//setFocusMarker("UNFOCUS",null,null,null);
					setFocus("UNFOCUS",performedAction,null,null,null);
					
					//Cancel all Timer
					cameraFocusTimer.cancel();
					longClickTimer.cancel();
					doubleTapTimer.cancel();
					isDoubleTapAction = false;
					
					firstPointer = event.getPointerId(0);
					secondPointer = event.getPointerId(1);
					firstPointerIndex = event.findPointerIndex(firstPointer);
					secondPointerIndex = event.findPointerIndex(secondPointer);
					//pointerDistance = Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex) - event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
					Float pointX = Math.abs(event.getX(firstPointerIndex) - event.getX(secondPointerIndex));
					Float pointY = Math.abs(event.getY(firstPointerIndex) - event.getY(secondPointerIndex));
						   //  +    Math.abs(event.getY(firstPointerIndex) - event.getY(secondPointerIndex))^2
					pointerDistance = (float) Math.sqrt((pointX*pointX) + (pointY*pointY));
					Log.i(TAG, "firstPointerIndex = "+firstPointerIndex+" : secondPointerIndex = "+secondPointerIndex);
				break;
				
				case MotionEvent.ACTION_DOWN:
//					if(!isDoubleTapAction)
//						doDoubleTap().start();
//					
//					if(isDoubleTapAction){
//						tapCount++;
//					}
					
					Log.i(TAG, "MotionEvent = ACTION_DOWN");
					performedAction = Field.ActionClick;
					
					
					firstPointer = event.getPointerId(0);
					firstPointerIndex = event.findPointerIndex(firstPointer);
					
//					if(isDoubleTapAction){
//						//takeAShot();
//						
//						if(view.getId() == R.id.cumPreviewBack && !isBackTaken){
//							cameraSide = "BACK";
//							takeAShot();
//						}
//						else if(view.getId() == R.id.cumPreviewFront && !isFrontTaken){
//							cameraSide = "FRONT";
//							takeAShot();
//						}
//						
//					}
//					else	
//					{
//						setFocus("UNFOCUS",performedAction,null,null,null);
//						setFocus("FOCUS", performedAction, event.getX(firstPointerIndex),event.getY(firstPointerIndex),view);
//					}
					
					if(touchAction != Field.ActionFocusing && touchAction != Field.ActionNothing){
						if(topTapCount < 1 && bottomTapCount <1 ){
							touchAction = performedAction;
							cameraFocusTimer.cancel();
							setFocus("UNFOCUS",performedAction,null,null,null);
							setFocus("FOCUS", performedAction, event.getX(firstPointerIndex),event.getY(firstPointerIndex),view);
						}
							
	
						if(view.getId() == R.id.cumPreviewBack){
							touchAction = performedAction;
							topTapCount++;
							if(!isBackTaken){
								//cameraFocusTimer.cancel();
								longClickTimer.start();
							}
						}
						else if(view.getId() == R.id.cumPreviewFront){
							touchAction = performedAction;
							bottomTapCount++;
							if(!isFrontTaken){
								//cameraFocusTimer.cancel();
								longClickTimer.start();
							}
						}
					}
				break;
				
				case MotionEvent.ACTION_UP:
					if(touchAction != Field.ActionFocusing && touchAction != Field.ActionNothing)
					if(performedAction == Field.ActionClick || performedAction == Field.ActionLongClick){

						performedAction = Field.ActionClickEnd;
						touchAction = performedAction;
						longClickTimer.cancel();
						//Log.i(TAG, "MotionEvent = ACTION_UP : inside2 = "+performedAction);
						//Instantiate Camera preview or Camera shot
						if(view.getId() == R.id.cumPreviewBack){
							
//							if(mCamera != null){
//								if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)){
//									if(topTapCount == 2 && !isBackTaken){
//										isDoubleTapAction = false;
//										bottomTapCount = 0;
//										topTapCount = 0;
//										takeAShot();
//									}
//								}
//								else{
//									isDoubleTapAction = false;
//									bottomTapCount = 0;
//									topTapCount = 0;
//									takeAShot();
//								}
//									
//							}
							
							
							
							if(isSavable && isBackTaken){
//								//isRetaking = true;
//								doubleTapTimer.cancel();
//								isDoubleTapAction = false;
								bottomTapCount = 0;
								topTapCount = 0;
								retakeImage("BACK");
							}
							else if(topTapCount == 2 && !isBackTaken && hasCameraFocus){
								isDoubleTapAction = false;
								bottomTapCount = 0;
								topTapCount = 0;
								takeAShot();
							}
							else if(!isBackTaken && !hasCameraFocus){
								isDoubleTapAction = false;
								bottomTapCount = 0;
								topTapCount = 0;
								takeAShot();
							}
							
							
						}
						else if(view.getId() == R.id.cumPreviewFront){
							if(isSavable && isFrontTaken){
								//doubleTapTimer.cancel();
								//isDoubleTapAction = false;
								bottomTapCount = 0;
								topTapCount = 0;
								retakeImage("FRONT");
							}
							
							else if(!isFrontTaken){
								isDoubleTapAction = false;
								bottomTapCount = 0;
								topTapCount = 0;
								takeAShot();
							}
							
//							else if(bottomTapCount == 2 && !isFrontTaken && hasCameraFocus){
//								isDoubleTapAction = false;
//								bottomTapCount = 0;
//								topTapCount = 0;
//								takeAShot();
//							}
//							
//							else if(!isFrontTaken && !hasCameraFocus){
//								isDoubleTapAction = false;
//								bottomTapCount = 0;
//								topTapCount = 0;
//								takeAShot();
//							}
							
						}
						
					}
				break;
				
				case MotionEvent.ACTION_POINTER_UP:
					Log.i(TAG, "MotionEvent = ACTION_POINTER_UP");
					//End event of zoom
					//Cancel the action, there's nothing more to do
				break;
				
				case MotionEvent.ACTION_MOVE:
					Log.i(TAG, "MotionEvent = ACTION_MOVE");
					//setFocusMarker("UNFOCUS");
					if(performedAction == Field.ActionZoom){
						
						if(event.getPointerCount() > 1){
							
							//Log.i(TAG, "Finger 1 = "+event.getX(firstPointerIndex));
							//Log.i(TAG, "Finger 2 = "+event.getX(secondPointerIndex));
							
							//pointerDistance = Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex) - event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
							Float pointXC = Math.abs(event.getX(firstPointerIndex) - event.getX(secondPointerIndex));
							Float pointYC = Math.abs(event.getY(firstPointerIndex) - event.getY(secondPointerIndex));
								   //  +    Math.abs(event.getY(firstPointerIndex) - event.getY(secondPointerIndex))^2
							changedPointerDistance = (float) Math.sqrt((pointXC*pointXC) + (pointYC*pointYC));
							//changedPointerDistance = Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex) - event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
							Log.i(TAG, "Distance of both fingers = "+pointerDistance+" : "+changedPointerDistance);
							if(pointerDistance > changedPointerDistance){
								//Do zoom
								doZoom(pointerDistance, changedPointerDistance);
								pointerDistance = changedPointerDistance;
							}
							else if(pointerDistance < changedPointerDistance){
								//Do zoom out
								doZoom(pointerDistance, changedPointerDistance);
								pointerDistance = changedPointerDistance;
							}
						}
						
					}
					
				break;
				
			}
			
			Log.i(TAG, "Number of points = "+event.getPointerCount()+" : Event = "+event.getAction());
			return true;
		}
		
	}
	
	public void setUntake(String cameraSide){
		  if(cameraSide == "BACK"){
			  isBackTaken = false;
		  }
		  else if(cameraSide == "FRONT"){
			  isFrontTaken = false;
		  }
	  }
	
	public void shareFunction(){
		Uri uri = Uri.parse("file://"+fileName);
		String shareBody = "Here is the share content body";
		sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/png");
		sharingIntent.putExtra(Intent.EXTRA_STREAM,uri);
		finish();
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	private void showFileChooser() {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("folder/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);

	    try {
	        startActivityForResult(
	                Intent.createChooser(intent, "Select a File to Upload"),
	                Field.FILE_SELECT_CODE);
	    } catch (android.content.ActivityNotFoundException ex) {
	        // Potentially direct the user to the Market with a Dialog
	        Toast.makeText(this, "Please install a File Manager.", 
	                Toast.LENGTH_SHORT).show();
	    }
//		Intent intent = new Intent(this, DirectoryPicker.class);
//		// optionally set options here
//		startActivityForResult(intent, DirectoryPicker.PICK_DIRECTORY);
	}
  
	public void takeAShot(){
		if(mCamera != null){
			try{
				mCamera.setErrorCallback(ec);
				mCamera.takePicture(null, null,getPic);
				

			}catch(Exception e){
				Log.i(TAG, "Error at capture button : e = "+e.getCause());
				Toast.makeText(getApplicationContext(),errorMessage,Field.SHOWTIME).show();
			}
			
		}
	}




	@Override
	public void colorChanged(int color) {
		PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(
		        COLOR_PREFERENCE_KEY, color).commit();
    	fontColor = color;
	}
	
}