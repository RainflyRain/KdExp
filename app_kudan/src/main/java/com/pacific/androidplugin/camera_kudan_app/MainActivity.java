package com.pacific.androidplugin.camera_kudan_app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Size;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import eu.kudan.ar.camera.MyCameraManager;
import eu.kudan.ar.UIWraper;
import eu.kudan.ar.input.JavaInput;
import eu.kudan.ar.KudanOutputWrapper;


/**
 * Main activity that checks and requests camera permissions from the user before starting the
 * KudanCV demo.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make the activity fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.camera_fragment);
       JavaInput.mCameraPreviewSize = new Size(640,480);
     //   JavaInput.mCameraPreviewSize = new Size(1920,1080);

        // Request camera permissions from the user if they are not currently set.
        if (null == savedInstanceState) {
            permissionsRequest();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Checks that the application has permission to use the camera and starts the demo if true.
     *
     * If false, requests camera permission from the user.
     */
    public void permissionsRequest() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA},
                    1);
        }
        else {
            setupFragment();
        }
    }


    /**
     * Called when the user responds to the permissions request. Starts the demo if the user has
     * granted the camera permission.
     *
     * If the user has denied permissions, this method throws a runtime exception.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {

                    setupFragment();
                }
                else {

                    throw new RuntimeException("Camera permissions must be granted to function.");
                }
            }
        }
    }

    /**
     * Activates the camera fragment to start the KudanCV demo.
     */
    private void setupFragment() {

        mKudanOutputWrapper = new KudanOutputWrapper() {
            @Override
            public void UpdateView(Bitmap cameraFrame, JavaInput.TrackerState trackerState, ArrayList<Point> trackedCorners) {
                mUIWrapper.UpdateView(cameraFrame, trackerState,trackedCorners);
            }
        };
        mCameraManager= new MyCameraManager(this,mKudanOutputWrapper);
        mUIWrapper.InitView(this,new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCameraManager.changeTrackerMethod();
            }
        });
    }

    public UIWraper mUIWrapper = new UIWraper();//UIholder
    private MyCameraManager mCameraManager ;//摄像机黑盒子（往kudan输入信息）
    private KudanOutputWrapper mKudanOutputWrapper;//kudan输出集合

    @Override
    public void onPause() {
        mCameraManager.teardown();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        SurfaceView surfaceView = mUIWrapper.getSurfaceView();
        mCameraManager.setup(surfaceView);
    }
}
