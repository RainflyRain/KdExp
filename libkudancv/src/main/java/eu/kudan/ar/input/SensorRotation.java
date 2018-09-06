package eu.kudan.ar.input;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by LightSnail on 2018/7/13.
 */

public class SensorRotation {

    /**
     * Objects for holding the current device rotation in the arbitracker coordinate system.
     */
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final float[] mRotationQuaternion = new float[4];

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        /**
         * Callback method for receiving new changes to the device rotation.
         *
         * @param event Object containing the device rotation data.
         */
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

                // Get the current device rotation.
                float temp1[] = new float[16];
                float temp2[] = new float[16];

                SensorManager.getRotationMatrixFromVector(
                        temp1 , event.values);

                // Remap the device rotation to the arbitracker coordinate system.
                SensorManager.remapCoordinateSystem(temp1, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_MINUS_X, temp2);

                // Convert the rotation matrix into a quaternion.
                double w = Math.sqrt(1.0 + temp2[0] + temp2[5] + temp2[10]) / 2.0;
                double w4 = (4.0 * w);
                double x = (temp2[9] - temp2[6]) / w4 ;
                double y = (temp2[2] - temp2[8]) / w4 ;
                double z = (temp2[4] - temp2[1]) / w4 ;

                mRotationQuaternion[0] = (float)w;
                mRotationQuaternion[1] = (float)x;
                mRotationQuaternion[2] = (float)y;
                mRotationQuaternion[3] = (float)z;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    };
    /**
     * Setup the rotation sensor for receiving data on the device orientation status.
     */
    public void setupRotationSensor(Activity activity) {
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(mSensorEventListener, mSensor, 30000);
    }

    /**
     * Stops the rotation sensor.
     */
    public void teardownRotationSensor() {
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensorManager = null;
        mSensor = null;
    }

    public float[] getRotationQuaterion() {
        return mRotationQuaternion;
    }
}
