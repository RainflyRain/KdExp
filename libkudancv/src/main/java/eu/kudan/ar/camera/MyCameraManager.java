//package eu.kudan.ar.camera;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraManager;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.support.v4.app.ActivityCompat;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//
//
//import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;
//
///**
// * Created by LightSnail on 2018/7/13.
// */
//
//public class MyCameraManager {
//
//    private MyCameraStateCallBack  myCameraStateCallBack  ;
//    private Activity mActivity;
//    /**
//     * Background thread that is responsible for receiving camera frames and rendering GUI elements.
//     */
//    private HandlerThread mBackgroundThread;
//
//    /**
//     * Background handler for running tasks on the background thread.
//     */
//    private Handler mBackgroundHandler;
//
//
//    public MyCameraManager( Activity activity ) {
//
//        mActivity = activity;
//        myCameraStateCallBack = new MyCameraStateCallBack(mActivity,mBackgroundHandler);
//    }
//
//
//    /**
//     * Sets up a new CameraDevice if camera permissions have been granted by the user.
//     */
//    private void setupCameraDevice(Activity activity) {
//
//        // Check for camera permissions.
//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            throw new RuntimeException("Camera permissions must be granted to function.");
//        }
//        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
//        try {
//            String[] cameras = manager.getCameraIdList();
//            // Find back-facing camera.
//            for (String camera : cameras) {
//                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(camera);
//
//                // Reject all cameras but the back-facing camera.
//                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) != LENS_FACING_BACK) {
//                    continue;
//                }
//                myCameraStateCallBack.tryAcquire();
//                // Open camera. Events are sent to the mStateCallback listener and handled on the background thread.
//                manager.openCamera(camera, myCameraStateCallBack.mStateCallback, mBackgroundHandler);
//            }
//
//        } catch (CameraAccessException e) {
//            throw new RuntimeException("Cannot access camera.");
//        }
//    }
//
//    //region startlifecycle
//    public void setup(SurfaceView surfaceView) {
//
//        myCameraStateCallBack.setupRotationSensor( );
//        setupBackgroundThread();
//        StartCallBack(surfaceView,mActivity);
//    }
//    private void setupBackgroundThread() {
//        mBackgroundThread = new HandlerThread("BackgroundCameraThread");
//        mBackgroundThread.start();
//        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
//    }
//    public void StartCallBack(SurfaceView surfaceView,final Activity activity) {
//
//        if (surfaceView.getHolder().getSurface().isValid()) {
//            setupCameraDevice(activity);
//        }
//        else {
//            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//                @Override
//                public void surfaceCreated(SurfaceHolder holder) {
//                    setupCameraDevice(activity);
//                }
//                @Override
//                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                }
//                @Override
//                public void surfaceDestroyed(SurfaceHolder holder) {
//                }
//            });
//        }
//    }
//    //endregion
//
//    //region endlifecycle
//    public void teardown() {
//        myCameraStateCallBack.teardownRotationSensor();
//        teardownCamera();
//        teardownBackgroundThread();
//    }
//    private void teardownCamera() {
//            myCameraStateCallBack.close();
//    }
//    private void teardownBackgroundThread() {
//
//        mBackgroundThread.quitSafely();
//        try {
//            mBackgroundThread.join();
//            mBackgroundThread = null;
//            mBackgroundHandler = null;
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public  void changeTrackerMethod() {
//        myCameraStateCallBack.changeTrackerMethod( );
//    }
//    //endregion
//}
