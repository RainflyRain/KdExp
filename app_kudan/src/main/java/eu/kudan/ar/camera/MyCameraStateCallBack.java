package eu.kudan.ar.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import eu.kudan.ar.input.JavaInput;
import eu.kudan.ar.KudanOutputWrapper;

/**
 * Created by LightSnail on 2018/7/13.
 */

public class MyCameraStateCallBack {


    private final Handler mBackgroundHandler;
    private final KudanOutputWrapper mKudanOutputWrapper;
    /**
     * A CaptureRequest.Builder for the camera preview.
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * A CaptureRequest generated by mPreviewRequestBuilder.
     */
    private CaptureRequest mPreviewRequest;
    /**
     * A reference to the ImageReader that receives new camera preview frames and processes tracking and detection.
     */
    private ImageReader mImageReader;
    /**
     * A reference to the opened CameraDevice.
     */
    private CameraDevice mCameraDevice;
    /**
     * A CaptureSession for the camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    private final Activity mActivity;
    private MyImageAvailPackage mMyImageAvailPackage;

    public MyCameraStateCallBack( Activity activity, Handler backgroundHandler,KudanOutputWrapper kudanOutput) {

        this.mKudanOutputWrapper = kudanOutput;
        this.mActivity = activity ;
        this.mBackgroundHandler = backgroundHandler;
        mMyImageAvailPackage = new MyImageAvailPackage( activity,kudanOutput);
    }
    /**
     * A callback for managing CameraDevice events.
     */
    /**
     * A Semaphore to prevent the camera simultaneously opening and closing.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    public final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {

            Log.i("CameraDevice", "CameraDevice Opened.");

            mCameraDevice = cameraDevice;
            mMyImageAvailPackage.initial();
            // Create the camera preview.
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

            Log.i("CameraDevice", "CameraDevice Disconnected.");

            // Release the Semaphore to allow the CameraDevice to be closed.
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {

            Log.e("CameraDevice", "CameraDevice Error.");
            // Release the Semaphore to allow the CameraDevice to be closed.
            mCameraOpenCloseLock.release();

            cameraDevice.close();
            mCameraDevice = null;

            // Stop the activity.
            if (null != mActivity) {
                mActivity.finish();
            }
        }
    };


    /**
     * Creates a new CameraCaptureSession for the camera preview.
     */
    private void createCameraPreviewSession() {

        try {
            // Create an ImageReader instance that buffers two camera images so there is always room for most recent camera frame.
            mImageReader = ImageReader.newInstance(JavaInput.mCameraPreviewSize.getWidth(), JavaInput.mCameraPreviewSize.getHeight(), ImageFormat.YUV_420_888, 2);

            // Handle all new camera frames received on the background thread.
            mImageReader.setOnImageAvailableListener(mMyImageAvailPackage.mImageAvailListener, mBackgroundHandler);

            // Set up a CaptureRequest.Builder with the output Surface of the ImageReader.
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mImageReader.getSurface());

            // Create the camera preview CameraCaptureSession.
            mCameraDevice.createCaptureSession(Arrays.asList(mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (mCameraDevice == null) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                // Finally, start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        null, mBackgroundHandler);

                                // Release the Semaphore to allow the CameraDevice to be closed.
                                mCameraOpenCloseLock.release();

                            }
                            catch (CameraAccessException e) {
                                throw new RuntimeException("Cannot access camera during CameraCaptureSession setup.");
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            throw new RuntimeException("Camera capture session configuration failed.");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            throw new RuntimeException("Cannot access camera during CameraCaptureSession setup.");
        }
    }

    public void close() {

        try {
        // Prevent the teardown from occuring at the same time as setup.
            mCameraOpenCloseLock.acquire();
            if (mCaptureSession != null) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (mImageReader != null) {
                mImageReader.close();
                mImageReader = null;
            }
        }
        catch (InterruptedException e) {
                e.printStackTrace();
        }finally {
                mCameraOpenCloseLock.release();
         }
    }

    public void tryAcquire() {

        try {
            mCameraOpenCloseLock.tryAcquire(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Camera open/close semaphore cannot be acquired");
        }

    }
    public void setupRotationSensor() {
        mMyImageAvailPackage.setupRotationSensor();
    }
    public void changeTrackerMethod() {
        mMyImageAvailPackage.changeTrackerMethod();
    }
    public void teardownRotationSensor() {
        mMyImageAvailPackage.teardownRotationSensor();
    }
}