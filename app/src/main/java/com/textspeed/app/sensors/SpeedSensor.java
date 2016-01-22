package com.textspeed.app.sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.Matrix;

/**
 * Created by crimson on 08.04.2015.
 */
public abstract class SpeedSensor {

    private long previousTimestamp = 0;
    private double time = 0;

    private double x = 0;
    private double y = 0;
    private double z = 0;

    private float[] identityMatrix = new float[16];
    private float[] rotationMatrix = new float[16];
    private float[] unRotationMatrix = new float[16];
    private float[] angleChange = new float[3];

    private float[] acceleration = new float[]{0,0,0,1f};
    private float[] accWorld = new float[]{0,0,0,1f};

    private final static float rad2a = (float) (180/Math.PI);

    private DataProvider accelerometerDataProvider = new DataProvider(Sensor.TYPE_LINEAR_ACCELERATION) {
        @Override
        public void onNewData(float[] values, long timestamp) {
            if(previousTimestamp != 0) {

                // converting nanoseconds to seconds
                time = (timestamp - previousTimestamp) * 0.000_000_000_1d;

                acceleration[0] = values[0];
                acceleration[1] = values[1];
                acceleration[2] = values[2];

                // calculating acceleration in world coordinates
                Matrix.multiplyMV(accWorld, 0, unRotationMatrix, 0, acceleration, 0);

                // getting speed in world coordinates
                x = time * accWorld[0];
                y = time * accWorld[1];
                z = time * accWorld[2];
                onNewSpeedData(x, y, z);

            }
            previousTimestamp = timestamp;
        }
    };

    private DataProvider rotationDataProvider = new DataProvider(Sensor.TYPE_ROTATION_VECTOR) {
        @Override
        public void onNewData(float[] values, long timestamp) {

            // calculating rotation matrix from rotation data
            SensorManager.getRotationMatrixFromVector(rotationMatrix, values);

            // calculating reverse rotation matrix
            Matrix.invertM(unRotationMatrix, 0, rotationMatrix, 0);

            if(calcAngles) {
                // calculating device rotation in world coordinates
                SensorManager.getAngleChange(angleChange, rotationMatrix, identityMatrix);

                onNewRotationAngles(angleChange[0], angleChange[1], angleChange[2]);
            }

        }
    };

    private boolean calcAngles = false;

    public SpeedSensor(boolean calcAngles) {
        this.calcAngles = calcAngles;
        Matrix.setIdentityM(identityMatrix, 0);
        Matrix.setIdentityM(rotationMatrix, 0);
    }

    public abstract void onNewSpeedData(double x, double y, double z);

    public abstract void onNewRotationAngles(double a, double b, double c);

    public void register(SensorManager sensorManager) {
        accelerometerDataProvider.register(sensorManager);
        rotationDataProvider.register(sensorManager);
    }

    public void unregister(SensorManager sensorManager) {
        accelerometerDataProvider.unregister(sensorManager);
        rotationDataProvider.unregister(sensorManager);
    }

}
