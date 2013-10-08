package com.cam.dualcam.utility;

public class Field {
	
	//Crop
	public static final int SET_AS = 1;
	public static final int CROP = 2;
	public static final int ROT_LEFT = 3;
	public static final int ROT_RIGHT = 4;
	
	//Basic Requests
	public static final int CAMERA_REQUEST = 100;
	public static final int VIDEO_REQUEST = 200;
	public static final int GALLERY_REQUEST = 300;
	public static final int CAMERA_CROP_REQUEST = 500;
	public static final int NON_CAMERA_CROP_REQUEST = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	//Tint
  	public static final double PI = 3.14159d;
  	public static final double FULL_CIRCLE_DEGREE = 360d;
  	public static final double HALF_CIRCLE_DEGREE = 180d;
  	public static final double RANGE = 256d;
  	//Sharpen from blog
  	final static int KERNAL_WIDTH = 3;
  	final static int KERNAL_HEIGHT = 3;
  	int[][] kernalBlur = {
  	   {0, -1, 0},
  	   {-1, 5, -1},
  	   {0, -1, 0}
  	};
  	
  	//Toast showtime
  	public static final int SHOWTIME = 5000;
	public static final int FILE_SELECT_CODE = 0;

	
	public static Integer ActionClick = 1000;
	public static Integer ActionClickEnd = 1001;
	public static Integer ActionZoom = 2000;
	public static Integer ActionLongClick = 3000;
	public static Integer ActionLongClickEnd = 3001;
	public static Integer ActionMove = 4000;
	public static Integer ActionDoubleTap = 5000;
	public static Integer ActionDoubleTapEnd = 5001;
	
	
	public static Integer ActionAutoFocus = 6000;
	public static Integer ActionFocusing = 6001;
	public static Integer ActionFocusingEnd = 6002;
	public static Integer ActionNothing = 9999;
  	
  	//Folder name
//  	public static final String APPNA
}

