package eu.kudan.ar;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

import eu.kudan.ar.input.JavaInput;

/**
 * Created by LightSnail on 2018/7/14.
 */

public interface  KudanOutputWrapper {

     void UpdateView(Bitmap cameraFrame, JavaInput.TrackerState trackerState, ArrayList<Point>  trackedCorners );
}
