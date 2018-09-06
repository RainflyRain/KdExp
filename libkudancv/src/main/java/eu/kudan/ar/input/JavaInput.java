package eu.kudan.ar.input;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.util.Size;

import java.util.ArrayList;

/**
 * Created by LightSnail on 2018/7/13.
 */

public class JavaInput {

    Activity mActivity;
    ArrayList<Point> trackedCorners = new ArrayList<>(4);//输出四个点的信息
    private SensorRotation mSensorRotationManager = new SensorRotation();
    /**
     * Dimensions of the camera preview.
     */
    public static Size mCameraPreviewSize = new Size(1920, 1080);
    /**
     * Describes the current state of tracking in the most recently processed camera frame.
     */
    public TrackerState mTrackerState = TrackerState.IMAGE_DETECTION;

    public void setupRotationSensor( ) {
        mSensorRotationManager.setupRotationSensor(mActivity);
    }
    public void teardownRotationSensor() {
        mSensorRotationManager.teardownRotationSensor();
    }

    /**
     * Possible states of tracking available during camera frame processing.
     */
    public enum TrackerState {
        IMAGE_DETECTION,
        IMAGE_TRACKING,
        ARBITRACK
    }
    public JavaInput(Activity activity ){
        mActivity = activity;
        for (int i = 0;i < 4;i++) {
            trackedCorners.add(new Point());
        }
    }
    //region Frame Processing Methods

    /**
     * Processes tracking on a camera frame's data.
     *
     * @param data Array containing the camera frame luma data to be processed.
     * @param width Width of the camera frame.
     * @param height Height of the camera frame.
     * @param currentState The current tracking state of the system.
     * @return The new tracking state of the system.
     */
    public TrackerState processTracking(byte[] data, int width, int height, TrackerState currentState) {


        float[] mRotationQuaternion = mSensorRotationManager.getRotationQuaterion();
        float[] trackedData = null;
        TrackerState newState = currentState;

        // Perform image detection and tracking.
        if (currentState != TrackerState.ARBITRACK) {

            Log.e("LightSnail","processImageTrackerFrame   "+data.length+","+width+","+height+",");
            // Native call to the image tracking and detection object.
            trackedData = processImageTrackerFrame(
                    data,
                    width,
                    height,
                    1, /*One channel as we are processing luma data only*/
                    0,
                    false
            );

            if (trackedData != null) {
                newState = TrackerState.IMAGE_TRACKING;
            }
            else {
                newState = TrackerState.IMAGE_DETECTION;
            }
        }

        // Else perform markerless tracking.
        else if (currentState == TrackerState.ARBITRACK) {
            // Inverse the device rotation quaternion to counteract it's rotation in the tracker.
            float w = mRotationQuaternion[0];
            float x = mRotationQuaternion[1];
            float y = mRotationQuaternion[2];
            float z = mRotationQuaternion[3];

            float norm = w * w + x * x + y * y + z * z;

            if (norm > 0.0) {
                float invNorm = 1.0f / norm;
                x *= -invNorm;
                y *= -invNorm;
                z *= -invNorm;
                w *= invNorm;
            }

            mRotationQuaternion[0] = w;
            mRotationQuaternion[1] = x;
            mRotationQuaternion[2] = y;
            mRotationQuaternion[3] = z;

            // Native call to the markerless tracking object.
            trackedData = processArbiTrackerFrame(data, mRotationQuaternion, width, height, 1, 0, false);
        }

        if (trackedData != null) {

            Log.e("LightSnail","trackedData has ");
            if(flag == false){
                flag = true;
                Log.e("LightSnail","tryResetSensor()");
//                LightSnailManager.getInstance().tryResetSensor();
            }
//            LightSnailManager.getInstance().setCameraMatrix(getK());
//            LightSnailManager.getInstance().setTranslateMatrix(getPosition());
//            LightSnailManager.getInstance().setRotationMatrix(getOrientation());
//            LightSnailManager.getInstance().setRotationMatrixByQuaternion(getOrientationQuaterion());
            //      Log.d("lightSnail","trackerData.length = "+trackedData.length);
            // Set the supplied point ArrayList values to the returned projected tracking coordinates.
            trackedCorners.get(0).set(Math.round(trackedData[2]), Math.round(trackedData[3]));
            trackedCorners.get(1).set(Math.round(trackedData[4]), Math.round(trackedData[5]));
            trackedCorners.get(2).set(Math.round(trackedData[6]), Math.round(trackedData[7]));
            trackedCorners.get(3).set(Math.round(trackedData[8]), Math.round(trackedData[9]));


        }else{
            flag = false;
        }

        return newState;
    }
    private boolean flag = false;


    //endregion

    //region Utility Methods

    /**
     * Adds an image trackable image to the native image tracker object.
     *
     * @param resourceID A reference to the image asset that should be used as a trackable.
     * @param name The name of the trackable that should be used internally for ID.
     */
    public void addTrackable(int resourceID, String name) {

        // Create a bitmap from the resource file.
        Bitmap image = BitmapFactory.decodeResource(mActivity.getResources(), resourceID);

        // Pass the bitmap to JNI for addition to the image tracker.
        boolean success = addTrackableToImageTracker(image, name);

        if (!success) {
            throw new RuntimeException("Trackable could not be added to image tracker.");
        }
    }

    //endregion

    //region UI Callback Methods

    /**
     * Listener method for changing the tracking state of the system based on user input.
     *
     */
    public TrackerState changeTrackerMethod(TrackerState trackerState ) {

        TrackerState newState = trackerState;
        // Synchronize with the tracker state to prevent changes to state mid-processing.
        synchronized (trackerState) {

            if (trackerState == TrackerState.IMAGE_DETECTION) {

                startArbiTracker(false);

                newState = TrackerState.ARBITRACK;

            } else if (trackerState == TrackerState.IMAGE_TRACKING) {

                startArbiTracker(true);

                newState = TrackerState.ARBITRACK;

            } else if (trackerState == TrackerState.ARBITRACK) {

                stopArbiTracker();

                newState = TrackerState.IMAGE_DETECTION;
            }
        }
        return newState;
    }

    //endregion

    //region Native Methods

    /**
     * Loads the required JNI native library on creation of CameraFragment.
     */
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * Initialise the native image tracker object.
     *
     * @param key The KudanCV API key.
     * @param width The width of camera frames that will be processed.
     * @param height The height of camera frames that will be processed.
     */
    public native void initialiseImageTracker(String key, int width, int height);

    /**
     * Initialise the native markerless tracker object.
     *
     * @param key The KudanCV API key.
     * @param width The width of camera frames that will be processed.
     * @param height The height of camera frames that will be processed.
     */
    public native void initialiseArbiTracker(String key, int width, int height);

    /**
     * Starts the native markerless tracker ready for tracking.
     *
     * @param startFromImageTrackable Should the initial markerless primitive be started at the position of the currently tracked image trackable.
     */
    private native void startArbiTracker(boolean startFromImageTrackable);

    /**
     * Stops the native markerless tracker.
     */
    private native void stopArbiTracker();

    /**
     * Adds an image as a trackable to the native image tracker object.
     *
     * @param image Bitmap containing a Bitmap.Config.ARGB_8888 image to be used as a trackable.
     * @param name The name of the trackable to be used for internal ID.
     * @return Whether the trackable was added to the image tracker object successfully.
     */
    private native boolean addTrackableToImageTracker(
            Bitmap image,
            String name);

    /**
     * Processes an image through the native image tracker object and returns tracking data.
     *
     * @param image Array containing the camera frame data.
     * @param width The width of the camera image.
     * @param height The height of the camera image.
     * @param channels The number of channels contained in the camera frame.
     * @param padding Padding in the camera frame data.
     * @param requiresFlip Whether the camera frame should be flipped before tracking.
     * @return Array containing the screen-space projected corner coordinates of the tracking primitive.
     */
    private native float[] processImageTrackerFrame(
            byte[] image,
            int width,
            int height,
            int channels,
            int padding,
            boolean requiresFlip);

    private native float[] getK();

    public native float[] getPosition();

    public native float[] getOrientation();

    public native float[] getOrientationQuaterion();
    /**
     * Processes an image through the native markerless tracker object and returns tracking data.
     *
     * @param image Array containing the camera frame data.
     * @param gyroOrientation Array containing the device rotation quaternion values in the order w, x, y, z.
     * @param width The width of the camera image.
     * @param height The height of the camera image.
     * @param channels The number of channels contained in the camera frame.
     * @param padding Padding in the camera frame data.
     * @param requiresFlip Whether the camera frame should be flipped before tracking.
     * @return Array containing the screen-space projected corner coordinates of the tracking primitive.
     */
    private native float[] processArbiTrackerFrame(
            byte[] image,
            float[] gyroOrientation,
            int width,
            int height,
            int channels,
            int padding,
            boolean requiresFlip);

    //endregion
}
