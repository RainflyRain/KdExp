//package eu.kudan.ar.camera;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Point;
//import android.graphics.RectF;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//import eu.kudan.ar.CameraSurfaceView;
//import eu.kudan.ar.Drawing;
//import eu.kudan.ar.R;
//import eu.kudan.ar.input.JavaInput;
//
///**
// * Created by LightSnail on 2018/7/12.
// */
//
//public class UIWraper {
//
//    /**
//     * The CameraSurfaceView on which the camera preview frames and GUI are rendered to.
//     */
//    private CameraSurfaceView mSurfaceView;
//
//    /**
//     * A TextView for displaying the current tracking state.
//     */
//    private TextView mStatusLabel;
//
//    /**
//     * A Button that allows the user to change the current tracking type.
//     */
//    private Button mButton;
//    /**
//     * Pre-allocated objects for transforming primitive drawing coordinates from camera frame space
//     * to screen space.
//     */
//    RectF mSrcRect = new RectF();
//    RectF mDstRect = new RectF();
//    Matrix mCanvasTransform = new Matrix();
//    Activity activity;
//    /**
//     * Callback listener for mSurfaceView to manage Surface lifecycle events.
//     */
//    public void InitView(Activity activity,View.OnClickListener vo) {
//        this.activity = activity;
//        mSurfaceView = (CameraSurfaceView) activity.findViewById(R.id.surface_view);
//        mSurfaceView.setAspectRatio(JavaInput.mCameraPreviewSize.getWidth(), JavaInput.mCameraPreviewSize.getHeight());
//
//        mStatusLabel = (TextView) activity.findViewById(R.id.status_label);
//
//        mButton = (Button) activity.findViewById(R.id.button);
//        mButton.setOnClickListener(vo);
//    }
//
//
//    public void UpdateView(Bitmap cameraFrame, JavaInput.TrackerState currentState, ArrayList<Point> primitiveCorners) {
//
//        // Define UI element values.
//        final int buttonColor;
//        final String buttonText;
//        final String primitiveLabel;
//        final String statusLabel;
//        final Drawing.DrawingPrimitive primitive;
//
//        if (currentState == JavaInput.TrackerState.IMAGE_DETECTION) {
//
//            buttonColor = Color.rgb(255, 162, 0);
//            buttonText = "Start Arbitrack";
//            primitiveLabel = "";
//            statusLabel = "Looking for image...";
//
//            primitive = Drawing.DrawingPrimitive.DRAWING_NOTHING;
//
//        }
//        else if (currentState == JavaInput.TrackerState.IMAGE_TRACKING) {
//
//            buttonColor = Color.BLUE;
//            buttonText = "Start Arbitrack from marker";
//            primitiveLabel = "Lego";
//            statusLabel = "Tracking image";
//
//            primitive = Drawing.DrawingPrimitive.DRAWING_RECTANGLE;
//
//        } else if (currentState == JavaInput.TrackerState.ARBITRACK) {
//
//            buttonColor = Color.GREEN;
//            buttonText = "Stop Arbitrack";
//            primitiveLabel = "Arbitrack";
//            statusLabel = "Running arbitrack";
//
//            primitive = Drawing.DrawingPrimitive.DRAWING_GRID;
//        } else {
//
//            buttonColor = Color.TRANSPARENT;
//            buttonText = "";
//            primitiveLabel = "";
//            statusLabel = "";
//            primitive = Drawing.DrawingPrimitive.DRAWING_NOTHING;
//        }
//        // Update Android GUI.
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                mButton.setBackgroundColor(buttonColor);
//                mButton.setText(buttonText);
//
//                mStatusLabel.setText(statusLabel);
//                mStatusLabel.setTextColor(buttonColor);
//            }
//        });
//        // Draw everything to screen. Drawing is achieved with Android's Canvas classes, if high
//        // performance is required, consider using OpenGL to draw instead.
//
//        // Calculate scaling that needs to be applied to the canvas to fit drawing to screen.
//        mSrcRect.set(0, 0, JavaInput.mCameraPreviewSize.getWidth(), JavaInput.mCameraPreviewSize.getHeight());
//        mDstRect.set(0, 0, mSurfaceView.getWidth(), mSurfaceView.getHeight());
//
//
//        mCanvasTransform.setRectToRect(mSrcRect, mDstRect, Matrix.ScaleToFit.END);
//
//        mCanvasTransform.reset();
//
//
//
//        mCanvasTransform.setTranslate(-JavaInput.mCameraPreviewSize.getWidth()/2f,-JavaInput.mCameraPreviewSize.getHeight()/2f);
//        mCanvasTransform.postScale(mSurfaceView.getWidth()*1f/JavaInput.mCameraPreviewSize.getWidth(),mSurfaceView.getHeight()*1f/JavaInput.mCameraPreviewSize.getHeight());
//        mCanvasTransform.postTranslate(mSurfaceView.getWidth()/2f,mSurfaceView.getHeight()/2f);
//
//        // Lock the CameraSurfaceView Surface for drawing.
//        Canvas canvas = mSurfaceView.getHolder().getSurface().lockCanvas(mSurfaceView.getClipBounds());
//
//        // Draw the background camera image.
//        Drawing.drawBackground(
//                canvas,
//                cameraFrame
//        );
//        // Draw the tracking primitive.
//        Drawing.drawPrimitive(
//                canvas,
//                mCanvasTransform,
//                primitive,
//                primitiveCorners.get(0),
//                primitiveCorners.get(1),
//                primitiveCorners.get(2),
//                primitiveCorners.get(3),
//                primitiveLabel
//        );
//
//        // Unlock the CameraSurfaceView Surface to render to screen.
//        mSurfaceView.getHolder().getSurface().unlockCanvasAndPost(canvas);
//    }
//
//    public SurfaceView getSurfaceView() {
//        return mSurfaceView;
//    }
//}
