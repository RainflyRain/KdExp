//package eu.kudan.ar.camera;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.Point;
//import android.media.Image;
//import android.media.ImageReader;
//
//import java.nio.ByteBuffer;
//import java.util.ArrayList;
//
//import eu.kudan.ar.MainActivity;
//import eu.kudan.ar.R;
//import eu.kudan.ar.input.ImageInputLifeCycle;
//import eu.kudan.ar.input.JavaInput;
//
///**
// * Created by LightSnail on 2018/7/13.
// */
//
//class MyImageAvailPackage {
//
//    private   MainActivity mainActivity ;
//    private ImageInputLifeCycle imageInputLifeCycle;
//    /**
//     * Pre-allocated Point objects that retain the projected, screen-space corner coordinates of the tracked object.
//     */
//    private ArrayList<Point> trackedCorners = new ArrayList<>(4);
//    public MyImageAvailPackage(Activity activity){
//        mainActivity = (MainActivity) activity;
//        imageInputLifeCycle = new ImageInputLifeCycle(activity);
//        for (int i = 0;i < 4;i++) {
//            trackedCorners.add(new Point());
//        }
//    }
//    /**
//     * Callback listener for ImageReader that handles new camera preview frames received from the CameraDevice.
//     */
//    public ImageReader.OnImageAvailableListener mImageAvailListener = new ImageReader.OnImageAvailableListener() {
//
//        /**
//         * Pre-allocated Bitmap object for holding luma data from the most recent camera frame.
//         */
//        Bitmap cameraFrame = Bitmap.createBitmap(JavaInput.mCameraPreviewSize.getWidth(), JavaInput.mCameraPreviewSize.getHeight(), Bitmap.Config.ALPHA_8);
//
//        /**
//         * Pre-allocated byte array for holding the raw luma data of the  most recent camera frame.
//         */
//        byte[] cameraFrameData = new byte[JavaInput.mCameraPreviewSize.getWidth() * JavaInput.mCameraPreviewSize.getHeight()];
//
//        private byte[] b = new byte[1];
//        /**
//         * Callback method for handling new camera preview frames sent from the CameraDevice.
//         *
//         * @param reader The ImageReader receiving the new camera frame.
//         */
//        @Override
//        public void onImageAvailable(ImageReader reader) {
//
//            // Synchronize with the tracker state to prevent changes to state mid-processing.
//            synchronized (b) {
//
//                Image currentCameraImage = reader.acquireLatestImage();
//                // Return if no new camera image is available.
//                if (currentCameraImage == null) {
//                    return;
//                }
//                int width = currentCameraImage.getWidth();
//                int height = currentCameraImage.getHeight();
//                // Get the buffer holding the luma data from the YUV-format image.
//                ByteBuffer buffer = currentCameraImage.getPlanes()[0].getBuffer();
//                // Push the luma data into a byte array.
//                buffer.get(cameraFrameData);
//                // Update the cameraFrame bitmap with the new image data.
//                buffer.rewind();
//                cameraFrame.copyPixelsFromBuffer(buffer);
//                // Process tracking based on the new camera frame data.
//                imageInputLifeCycle.processTracking(cameraFrameData, width, height, trackedCorners);
//
//                // Render the new frame and tracking results to screen.
//                mainActivity.mUIWrapper.UpdateView(cameraFrame, imageInputLifeCycle.getTrackerState(),trackedCorners);
//                // Clean up frame data.
//                buffer.clear();
//                currentCameraImage.close();
//            }
//        }
//    };
//
//
//    public void initial() {
//
//        // Initialise the native tracking objects.
//        imageInputLifeCycle.initialiseImageTracker();
//        imageInputLifeCycle.initialiseArbiTracker();
//
//        // Add the image trackable to the native image tracker.
//        imageInputLifeCycle.addTrackable(R.mipmap.lego, "lego");
//        imageInputLifeCycle.addTrackable(R.mipmap.building, "building");
//    }
//
//    public void setupRotationSensor() {
//        imageInputLifeCycle.setupRotationSensor( );
//    }
//
//    public void changeTrackerMethod() {
//        imageInputLifeCycle.changeTrackerMethod();
//    }
//
//    public void teardownRotationSensor() {
//        imageInputLifeCycle.teardownRotationSensor( );
//    }
//}
