package com.textspeed.app.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class DataProvider {

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            onNewData(event.values, event.timestamp);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private final int type;

    public DataProvider(int type) {
        this.type = type;
    }

    public void register(SensorManager sensorManager) {
        Sensor accelerometer = sensorManager.getDefaultSensor(type);
        sensorManager.registerListener(sensorEventListener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(SensorManager sensorManager) {
        sensorManager.unregisterListener(sensorEventListener);
    }

    public abstract void onNewData(float[] values, long timestamp);

}
