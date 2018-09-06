package eu.kudan.ar.input;

import android.app.Activity;
import android.graphics.Point;
import android.util.Size;

import java.util.ArrayList;


/**
 * Created by LightSnail on 2018/7/13.
 */

public class ImageInputLifeCycle {
    private final Activity mActivity;
    private  JavaInput mJavaInput;
    //region lifeCycle
    // Get the KudanCV API key from the Android Manifest.
    private String mApiKey ;

    /**  1.  构造函数     */
    public ImageInputLifeCycle(Activity activity) {
        this.mActivity = activity;
        mApiKey = KeyUtils.getAPIKey( mActivity);
        mJavaInput = new JavaInput(activity );
    }
    /**  2.  注册rotationSensor     */
    public void setupRotationSensor() {
        mJavaInput.setupRotationSensor( );
    }
    /**  3.  初始化ImageTracker     */
    public void initialiseImageTracker() {
        mJavaInput.initialiseImageTracker(mApiKey, JavaInput. mCameraPreviewSize.getWidth(), JavaInput.mCameraPreviewSize.getHeight());
    }
    /**  4.  初始化ArbiTracker     */
    public void initialiseArbiTracker() {
        mJavaInput.initialiseArbiTracker(mApiKey, JavaInput.mCameraPreviewSize.getWidth(), JavaInput.mCameraPreviewSize.getHeight());
    }
    /**  5.  初始化需要检测的图片 */
    public void addTrackable(int resId, String showString) {
        mJavaInput.addTrackable(resId, showString);
    }
    /**  6.  临时更换检测方法  */
    public void changeTrackerMethod() {
        mJavaInput.mTrackerState =  mJavaInput.changeTrackerMethod(mJavaInput.mTrackerState);
    }
    /**  7.   图片检测的底层实现
     * @param width 图片实际的宽
     * @param height 图片实际的高*/
    public void  processTracking(byte[] cameraFrameData, int width, int height){
        mJavaInput. mTrackerState = mJavaInput.processTracking(cameraFrameData,  width,  height, mJavaInput. mTrackerState);
    }
    /**  8.  注销rotationSensor     */
    public void teardownRotationSensor() {
        mJavaInput.teardownRotationSensor();
    }
    //endregion lifeCycle
    //region utils
    public JavaInput.TrackerState getTrackerState() {
        return  mJavaInput. mTrackerState;
    }

    public ArrayList<Point> getTrackerCorners() {
        return mJavaInput.trackedCorners;
    }

    public float[] getPosition() {
        return mJavaInput.getPosition();
    }
    public float[] getOrientation() {
        return mJavaInput.getOrientation();
    }
    public float[] getOrientationQuaterion() {
        return mJavaInput.getOrientationQuaterion();
    }
    //endregion utils

}
