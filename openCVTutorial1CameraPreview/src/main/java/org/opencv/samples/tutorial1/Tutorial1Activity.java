package org.opencv.samples.tutorial1;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.GCMParameterSpec;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.R.attr.height;
import static android.R.attr.width;


public class Tutorial1Activity extends Activity implements CvCameraViewListener2 {
    private Tutorial1Activity activity;
    private static final String TAG = "OCVSample::Activity";

    // FlagDraw
    private boolean flagDraw = false;
    private boolean flagFilter = false;
    private String FLAGDRAW = "FLAGDRAW";

    private CameraBridgeViewBase mOpenCvCameraView;
    private TextView imgTilte, imgDisp;

    private boolean mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;

    // A key for storing the index of the active image size.
    private static final String STATE_IMAGE_SIZE_INDEX = "imageSizeIndex";

    // Keys for storing the indices of the active filters.
    private static final String STATE_IMAGE_DETECTION_FILTER_INDEX = "imageDetectionFilterIndex";

    // The filters.
    ImageDetectionFilter chengpo,chiayi,summer_street;
    public static ImageDetectionFilter[] mImageDetectionFilters;

    //public ArrayList<ImageDetectionFilter> mImageDetectionFilters = new ArrayList<ImageDetectionFilter>();

    // The indices of the active filters.
    private int mImageDetectionFilterIndex;

    // Target found index.
    private int foundTargetIndex = -1;

    // The index of the active image size.
    private int mImgSizeIndex;

    private Camera mCamera;

    MyReceiver receiver = new MyReceiver();
    IntentFilter filter = new IntentFilter();
    IntentFilter filterRarray = new IntentFilter();

    Bundle bundle = new Bundle();
    private Mat rgba;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:


                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();

                    try {
                        chengpo = new ImageDetectionFilter(Tutorial1Activity.this,
                                R.drawable.chengpo);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: " + "chengpo");
                        e.printStackTrace();
                        break;
                    }


                    try {
                        chiayi = new ImageDetectionFilter(
                                Tutorial1Activity.this,
                                R.drawable.chiayi);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: "
                                + "chiayi");
                        e.printStackTrace();
                        break;
                    }


                    try {
                        summer_street = new ImageDetectionFilter(
                                Tutorial1Activity.this,
                                R.drawable.summer_street);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to load drawable: "
                                + "summer_street");
                        e.printStackTrace();
                        break;
                    }


                    mImageDetectionFilters = new ImageDetectionFilter[]{summer_street, chengpo, chiayi};



                    break;


                default:

                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public Tutorial1Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        filter.addAction("android.intent.action.screen");
        registerReceiver(receiver, filter);



        if (savedInstanceState != null) {

            mImageDetectionFilterIndex = savedInstanceState.getInt(
                    STATE_IMAGE_DETECTION_FILTER_INDEX, 0);
            mImgSizeIndex = savedInstanceState
                    .getInt(STATE_IMAGE_SIZE_INDEX, 0);

        } else {

            mImgSizeIndex = 0;
            mImageDetectionFilterIndex = 0;

        }

        setContentView(R.layout.tutorial1_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        imgTilte = (TextView) findViewById(R.id.imgTitle);
        imgDisp = (TextView) findViewById(R.id.imgDisp);


        mOpenCvCameraView.setVisibility(View.VISIBLE);

        setPictureSize();

        //camera_frame = mOpenCvCameraView;


        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 註冊 registerReceiver



        //setPictureSize();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();


        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();

            unregisterReceiver(receiver);
        }


    }


//    public void onCameraViewStarted(int width, int height) {
//        android.hardware.Camera.Size res = mOpenCvCameraView.getResolutionList().get(((Tutorial1Activity) mOpenCvCameraView).getResolutionList().size()-1);
//        mOpenCvCameraView.setResolution(res);
//    }
//    
//    public List<Size> getResolutionList() {
//        return mCamera.getParameters().getSupportedPreviewSizes();
//    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        // 把傳入的 frame 接收
        Mat rgba = inputFrame.rgba();
        long rgbaMat = rgba.getNativeObjAddr();







        // StartService 後 取 rgba
        Intent intent = new Intent(Tutorial1Activity.this, Matcher.class);
        intent.putExtra("cameraFrame",rgbaMat);
        Log.d(TAG,"mImageDetectionFilters : "+mImageDetectionFilters.length);
        bundle.putSerializable("filters",mImageDetectionFilterIndex);
        intent.putExtra("filters",bundle);
        startService(intent);



//   for(mImageDetectionFilterIndex = 0 ; mImageDetectionFilterIndex < mImageDetectionFilters.length;mImageDetectionFilterIndex++){
//
//
//
//    		if(mImageDetectionFilterIndex == mImageDetectionFilters.length){
//    			mImageDetectionFilterIndex = 0;
//   		}
//
//
//
//    		mImageDetectionFilters[mImageDetectionFilterIndex].apply(rgba, rgba);
//
//	   			//captureBitmap(rgba);
//
//
//        	flagDraw = mImageDetectionFilters[mImageDetectionFilterIndex].targetFound();
//
//        	Log.d(FLAGDRAW, "flagDraw : "+ mImageDetectionFilterIndex);
//        	Log.d(FLAGDRAW, "flagDraw : "+ flagDraw);
//
//
//
//        		if(flagDraw){
//
//        			foundTargetIndex = mImageDetectionFilterIndex;
//        			//mImageDetectionFilters[foundTargetIndex].apply(rgba, rgba);
//            		//flagDraw = mImageDetectionFilters[mImageDetectionFilterIndex].targetFound();
//
//            		Log.d(FLAGDRAW, "!!!!flagDraw : "+ foundTargetIndex);
//            		Log.d(FLAGDRAW, "!!!!flagDraw : "+ flagDraw);
//
//            		switch (foundTargetIndex) {
//            		case 2:
//						// 設定文字說明
//						Thread chiayi = new Thread(new Runnable() {
//
//							@Override
//							public void run() {
//
//								mHandler.sendEmptyMessage(2);
//
//							}
//
//						});
//						chiayi.start();
//
//						break;
//
//
//					case 1:
//						// 設定文字說明
//						Thread chengpo = new Thread(new Runnable() {
//
//							@Override
//							public void run() {
//
//								mHandler.sendEmptyMessage(1);
//
//							}
//
//						});
//						chengpo.start();
//
//						break;
//
//					case 0:
//						// 設定文字說明
//						Thread summer_street = new Thread(new Runnable() {
//
//							@Override
//							public void run() {
//
//								mHandler.sendEmptyMessage(0);
//
//							}
//
//						});
//						summer_street.start();
//						break;
//
//
//
//					}
//
//
//        		}
//
//    		}
        return rgba;
    }

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//        	 if(msg.what == 2) {
//             	imgTilte.setText("嘉義街外");
//             	imgDisp.setText("藝術家： 陳澄波"+"\n"+"年代： 1927");
//             }
//
//            if(msg.what == 1) {
//            	imgTilte.setText("廟口");
//            	imgDisp.setText("藝術家： 陳澄波");
//            }
//
//            if(msg.what == 0){
//            	imgTilte.setText("夏日街景");
//            	imgDisp.setText("藝術家： 陳澄波"+"\n"+"年代： 1927");
//            }
//
//
//            super.handleMessage(msg);
//        }
//    };

    @Override
    public void onCameraViewStarted(int width, int height) {
        // TODO Auto-generated method stub


    }

    private void setPictureSize() {

        int  cameras = Camera.getNumberOfCameras();
        if(cameras >1) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }else if(cameras == 1){
            mCamera= Camera.open();
        }

        Camera.Parameters parameters = mCamera.getParameters();


        for (int i = 0; i < parameters.getSupportedPictureSizes().size(); i++) {

            Camera.Size size = parameters.getSupportedPictureSizes().get(i);
            Log.d(TAG, size.width + " x " + size.height);

            if (1024 == size.width && size.height == 576) {
                parameters.setPreviewSize(size.width, size.height);
                parameters.setPictureSize(size.width, size.height);


                mOpenCvCameraView.setMaxFrameSize(size.width, size.height);

                break;
            }
        }

    }


//	private void captureBitmap(Mat mRgba ){
//		Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_RGB2BGR565);
//		try {
//			Bitmap bitmap = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.RGB_565);
//			Utils.matToBitmap(mRgba, bitmap);
//			//Drawable drawable = new BitmapDrawable(bitmap);
//
//			canvas.setBitmap(bitmap);
//
//			mOpenCvCameraView.setVisibility(View.VISIBLE);
//			mOpenCvCameraView.draw(canvas);
//
//			mOpenCvCameraView.invalidate();
//			//camera_frame.draw(canvas);
//			//camera_frame.invalidate();
//		}catch(Exception ex){
//			System.out.println(ex.getMessage());
//		}
//	}








    public class MyReceiver extends BroadcastReceiver {
        //自定义一个广播接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //System.out.println("OnReceiver");
            Bundle bundle = intent.getExtras();
            String title = bundle.getString("title");
            String des = bundle.getString("des");

            imgTilte.setText(title);
            imgDisp.setText(des);
            //处理接收到的内容

        }

        public MyReceiver() {
            if (flagFilter) {
                System.out.println("MyReceiver");
                //构造函数，做一些初始化工作，本例中无任何作用
            }
        }
    }

}
