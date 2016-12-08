package org.opencv.samples.tutorial1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;

public class Matcher extends Service {

    private String Tag = "Service";

    Tutorial1Activity activity = new Tutorial1Activity();
    Mat inputFrame = null;
    ImageDetectionFilter[] filters ;
    //ArrayList<ImageDetectionFilter> filters = new ArrayList<ImageDetectionFilter>()  ; // 取得 FilterArray;

    Intent BIntent = new Intent();
    Boolean flagDraw = false;
    int detectIndex = 0;


    public Matcher() {
        filters = activity.mImageDetectionFilters;

        Log.e(Tag, "filters = " + filters.length);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(Tag, "start onCreate~~~");

        // 傳入 參考圖片 array



        BIntent.setAction("android.intent.action.screen");//action与接收器相同





//        final ImageDetectionFilter chengpo;
//                    try {
//                        chengpo = new ImageDetectionFilter(activity,
//                                R.drawable.chengpo);
//                        filters.add(chengpo);
//                    } catch (IOException e) {
//                        Log.e(Tag, "Failed to load drawable: " + "chengpo");
//                        e.printStackTrace();
//
//                    }
//
//        Log.i(Tag, "filters = "+filters.size());

//                    final ImageDetectionFilter chiayi;
//                    try {
//                        chiayi = new ImageDetectionFilter(
//                                Tutorial1Activity.this,
//                                R.drawable.chiayi);
//                    } catch (IOException e) {
//                        Log.e(Tag, "Failed to load drawable: "
//                                + "chiayi");
//                        e.printStackTrace();
//                        break;
//                    }
//
//                    final ImageDetectionFilter summer_street;
//                    try {
//                        summer_street = new ImageDetectionFilter(
//                                Tutorial1Activity.this,
//                                R.drawable.summer_street);
//                    } catch (IOException e) {
//                        Log.e(Tag, "Failed to load drawable: "
//                                + "summer_street");
//                        e.printStackTrace();
//                        break;
//                    }
//

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub

        Log.i(Tag, "绑定");
        return null;


    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(Tag, "Services onStart");
        super.onStart(intent, startId);




         // 收檔 camera frame
        try {
            long addr = intent.getLongExtra("cameraFrame", 0);
            if(addr > 0) {
                inputFrame = new Mat(addr);

                Log.i(Tag, "inputFrame " + inputFrame.size());
                addr = 0;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        // 處理 camera frame
        processCameraFrame(inputFrame);


//       GoProcessCameraFraem.start();



        //handler.post(processCameraFrame);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //handler.removeCallbacks(processCameraFrame);


    }


//    private Runnable processCameraFrame = new Runnable() {
//        @Override
//        public void run() {
//            // 處理 InputFrame
//
//            Log.d("Service","processCameraFrame");
//
//            inputFrame = activity.getRgba();
//
//            Log.d(Tag,"getRgba");
//
//
//            int i=0;
//
//                Intent intent = new Intent();
//                intent.putExtra("i", i);
//
//                intent.setAction("android.intent.action.test");//action与接收器相同
//
//
//                    sendBroadcast(intent);
//
//
//
//
//
//
//
//                Log.i("TAG", String.valueOf(i));
//            }


    private void processCameraFrame(Mat inputFrame) {

        Log.d(Tag,"processCameraFrame");




        for(detectIndex = 0 ; detectIndex < filters.length;detectIndex++){
            Log.d(Tag, "detectIndex : "+ detectIndex);

            if(detectIndex == filters.length){
                detectIndex = 0;
                }

            filters[detectIndex].apply(inputFrame, inputFrame);

            flagDraw = filters[detectIndex].targetFound();

                Log.d(Tag, "flagDraw_index : "+ detectIndex);
                Log.d(Tag, "flagDraw : "+ flagDraw);

            if(flagDraw){
                showInfo(detectIndex);
            }

        }

    }

//            for(mImageDetectionFilterIndex = 0 ; mImageDetectionFilterIndex < mImageDetectionFilters.length;mImageDetectionFilterIndex++){
//
//
//
//                if(mImageDetectionFilterIndex == mImageDetectionFilters.length){
//                    mImageDetectionFilterIndex = 0;
//                }
//
//
//
//                mImageDetectionFilters[mImageDetectionFilterIndex].apply(rgba, rgba);
//
//                //captureBitmap(rgba);
//
//
//                flagDraw = mImageDetectionFilters[mImageDetectionFilterIndex].targetFound();
//
//                Log.d(FLAGDRAW, "flagDraw : "+ mImageDetectionFilterIndex);
//                Log.d(FLAGDRAW, "flagDraw : "+ flagDraw);
//
//
//
//                if(flagDraw){
//
//                    foundTargetIndex = mImageDetectionFilterIndex;
//                    //mImageDetectionFilters[foundTargetIndex].apply(rgba, rgba);
//                    //flagDraw = mImageDetectionFilters[mImageDetectionFilterIndex].targetFound();
//
//                    Log.d(FLAGDRAW, "!!!!flagDraw : "+ foundTargetIndex);
//                    Log.d(FLAGDRAW, "!!!!flagDraw : "+ flagDraw);
//
//                    switch (foundTargetIndex) {
//                        case 2:
//                            // 設定文字說明
//                            Thread chiayi = new Thread(new Runnable() {
//
//                                @Override
//                                public void run() {
//
//                                    mHandler.sendEmptyMessage(2);
//
//                                }
//
//                            });
//                            chiayi.start();
//
//                            break;
//
//
//                        case 1:
//                            // 設定文字說明
//                            Thread chengpo = new Thread(new Runnable() {
//
//                                @Override
//                                public void run() {
//
//                                    mHandler.sendEmptyMessage(1);
//
//                                }
//
//                            });
//                            chengpo.start();
//
//                            break;
//
//                        case 0:
//                            // 設定文字說明
//                            Thread summer_street = new Thread(new Runnable() {
//
//                                @Override
//                                public void run() {
//
//                                    mHandler.sendEmptyMessage(0);
//
//                                }
//
//                            });
//                            summer_street.start();
//                            break;
//
//
//
//                    }
//
//
//                }
//
//            }


    public void showInfo(int detectIndex){
        switch (detectIndex) {
            case 0: {
                BIntent.putExtra("title", "夏日街景");
                BIntent.putExtra("des","藝術家： 陳澄波"+"\n"+"年代： 1927");
                sendBroadcast(BIntent);
            }
            break;

            case 1: {
                BIntent.putExtra("title", "廟口");
                BIntent.putExtra("des","藝術家： 陳澄波");
                sendBroadcast(BIntent);
            }
            break;

            case 2: {
                BIntent.putExtra("title", "嘉義街外");
                BIntent.putExtra("des","藝術家： 陳澄波"+"\n"+"年代： 1927");
                sendBroadcast(BIntent);
            }
            break;
        }
    }

}
