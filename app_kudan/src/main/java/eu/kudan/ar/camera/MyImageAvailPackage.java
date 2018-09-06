package eu.kudan.ar.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import com.pacific.androidplugin.camera_kudan_app.MainActivity;
import com.pacific.androidplugin.camera_kudan_app.R;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import eu.kudan.ar.input.ImageInputLifeCycle;
import eu.kudan.ar.input.JavaInput;
import eu.kudan.ar.KudanOutputWrapper;

/**
 * Created by LightSnail on 2018/7/13.
 */

class MyImageAvailPackage {

    private MainActivity mainActivity;
    private ImageInputLifeCycle imageInputLifeCycle;
    /**
     * Pre-allocated Point objects that retain the projected, screen-space corner coordinates of the tracked object.
     */

    private KudanOutputWrapper mKudanOutputWrapper;
    public MyImageAvailPackage(Activity activity,KudanOutputWrapper kudanOutput){

        this.mKudanOutputWrapper = kudanOutput;
        mainActivity = (MainActivity) activity;
        imageInputLifeCycle = new ImageInputLifeCycle(activity);

        bitmap = BitmapFactory.decodeResource(mainActivity.getResources(),R.drawable.build_cap);
        bitmap = Bitmap.createScaledBitmap(bitmap,640,480,true);
        buffer = getYUVByBitmap(bitmap);

    }
    /**
     * Callback listener for ImageReader that handles new camera preview frames received from the CameraDevice.
     */
    public ImageReader.OnImageAvailableListener mImageAvailListener = new ImageReader.OnImageAvailableListener() {

        /**
         * Pre-allocated Bitmap object for holding luma data from the most recent camera frame.
         */
        Bitmap cameraFrame = Bitmap.createBitmap(JavaInput.mCameraPreviewSize.getWidth(), JavaInput.mCameraPreviewSize.getHeight(), Bitmap.Config.ALPHA_8);

        /**
         * Pre-allocated byte array for holding the raw luma data of the  most recent camera frame.
         */
        byte[] cameraFrameData = new byte[JavaInput.mCameraPreviewSize.getWidth() * JavaInput.mCameraPreviewSize.getHeight()];

        private byte[] b = new byte[1];
        /**
         * Callback method for handling new camera preview frames sent from the CameraDevice.
         *
         * @param reader The ImageReader receiving the new camera frame.
         */
        @Override
        public void onImageAvailable(ImageReader reader) {

            doKudan(reader );

//            synchronized (b) {
//
//                if(buffer != null){
//
//                    imageInputLifeCycle.processTracking(buffer, bitmap.getWidth(), bitmap.getHeight());
//                    ArrayList<Point> trackerCorners = imageInputLifeCycle.getTrackerCorners();
//
//                    float[] position = imageInputLifeCycle.getPosition();
//                    float[] orientation = imageInputLifeCycle.getOrientation();
//                    float[] orientationQuaterion = imageInputLifeCycle.getOrientationQuaterion();
//
////                    Log.i("@App fei", "onImageAvailable: "+imageInputLifeCycle.getTrackerCorners());
//                    mKudanOutputWrapper.UpdateView(bitmap, imageInputLifeCycle.getTrackerState(),imageInputLifeCycle.getTrackerCorners()  );
//                }
//            }
        }
        public void doKudan(ImageReader reader){
            // Synchronize with the tracker state to prevent changes to state mid-processing.
            synchronized (b) {

                Log.e("LightSnail","kudan curentn "+Thread.currentThread().getName());
                Image currentCameraImage = reader.acquireLatestImage();
                // Return if no new camera image is available.
                if (currentCameraImage == null) {
                    return;
                }
                int width = currentCameraImage.getWidth();
                int height = currentCameraImage.getHeight();
                // Get the buffer holding the luma data from the YUV-format image.
                ByteBuffer buffer = currentCameraImage.getPlanes()[0].getBuffer();
                // Push the luma data into a byte array.
                buffer.get(cameraFrameData);
                // Update the cameraFrame bitmap with the new image data.
                buffer.rewind();
                // Process tracking based on the new camera frame data.
                imageInputLifeCycle.processTracking(cameraFrameData, width, height);

                cameraFrame.copyPixelsFromBuffer(buffer);
                // Render the new frame and tracking results to screen.
                mKudanOutputWrapper.UpdateView(cameraFrame, imageInputLifeCycle.getTrackerState(),imageInputLifeCycle.getTrackerCorners()  );

                float[] position = imageInputLifeCycle.getPosition();
                float[] orientation = imageInputLifeCycle.getOrientation();
                float[] orientationQuaterion = imageInputLifeCycle.getOrientationQuaterion();

                if (position != null){
                    Log.i("@app position", position[0]+","+position[1]+","+position[2]);
                }
//                if (orientation != null){
//                    Log.i("@app orientation", +orientation[0]+","+orientation[1]+","+orientation[2]+","+orientation[3]+","
//                            +orientation[4]+","+orientation[5]+","+orientation[6]+","+orientation[7]+","+orientation[8]);
//                }
                if (orientationQuaterion != null){
                    Log.i("@app Quaterion", orientationQuaterion[0]+","+orientationQuaterion[1]+","+orientationQuaterion[2]+","+orientationQuaterion[3]+",");
                }

                // Clean up frame data.
                buffer.clear();
                currentCameraImage.close();
            }
        }
    };
    byte[] buffer;
    Bitmap bitmap ;


    /*
	 * 获取位图的YUV数据
	 */
    public static byte[] getYUVByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = width * height;

        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // byte[] data = convertColorToByte(pixels);
        byte[] data = rgb2YCbCr420(pixels, width, height);

        return data;

    }
    public static byte[] rgb2YCbCr420(int[] pixels, int width, int height) {
        int len = width * height;
        // yuv格式数组大小，y亮度占len长度，u,v各占len/4长度。
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 屏蔽ARGB的透明度值
                int rgb = pixels[i * width + j] & 0x00FFFFFF;
                // 像素的颜色顺序为bgr，移位运算。
                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;
                // 套用公式
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                // rgb2yuv
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.147 * r - 0.289 * g + 0.437 * b);
                // v = (int) (0.615 * r - 0.515 * g - 0.1 * b);
                // RGB转换YCbCr
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.1687 * r - 0.3313 * g + 0.5 * b + 128);
                // if (u > 255)
                // u = 255;
                // v = (int) (0.5 * r - 0.4187 * g - 0.0813 * b + 128);
                // if (v > 255)
                // v = 255;
                // 调整
                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);
                // 赋值
                yuv[i * width + j] = (byte) y;
                yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                yuv[len + +(i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }

    public void initial() {

        // Initialise the native tracking objects.
        imageInputLifeCycle.initialiseImageTracker();
        imageInputLifeCycle.initialiseArbiTracker();

        // Add the image trackable to the native image tracker.
//        imageInputLifeCycle.addTrackable(R.mipmap.lego, "lego");
//        imageInputLifeCycle.addTrackable(R.mipmap.building, "building");
        imageInputLifeCycle.addTrackable(R.drawable.building,"LightSnail_Building");
    }

    public void setupRotationSensor() {
        imageInputLifeCycle.setupRotationSensor( );
    }

    public void changeTrackerMethod() {
        imageInputLifeCycle.changeTrackerMethod();
    }

    public void teardownRotationSensor() {
        imageInputLifeCycle.teardownRotationSensor( );
    }
}
