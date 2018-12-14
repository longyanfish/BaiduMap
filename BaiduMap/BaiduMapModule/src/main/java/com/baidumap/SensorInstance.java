package com.baidumap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 传感器工具类，手机的方向决定图标的指向
 */

public class SensorInstance implements SensorEventListener {

    private Context mContext;
    private SensorManager mSensorManager;

    public SensorInstance(Context context) {
        mContext = context;
    }

    public void start() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            //传感器本身Sensor
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            if (sensor != null) {
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = event.values[SensorManager.DATA_X];
            if (mListener != null) {
                mListener.onOrientation(x);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private OnOrientationChangedListener mListener;

    public void setOnOrientationChangedListener(OnOrientationChangedListener listener) {
        mListener = listener;
    }

    public static interface OnOrientationChangedListener {
        void onOrientation(float x);
    }
}
