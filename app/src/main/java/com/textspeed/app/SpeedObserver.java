package com.textspeed.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import com.textspeed.app.sensors.LocationAccuracy;
import com.textspeed.app.sensors.LocationData;
import com.textspeed.app.sensors.LocationDataProvider;
import com.textspeed.app.sensors.SpeedData;
import com.textspeed.app.sensors.SpeedSensor;

/**
 * Created by crimson on 04.04.2015.
 */
public class SpeedObserver {

    private final static long FRESH_GPS_TIME = 20 * 60 * 1000; // 20 minutes

    private final static String GPS_KEY = "gps_precision";
    private final static String ACC_KEY = "use_accelerometer";

    private LocationData locationData;
    private boolean accelerometerEnabled;

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if(key.equals(GPS_KEY)) {
                        locationDataProvider.setPrecision(locationManager,
                                sharedPreferences.getString(GPS_KEY, "high"));
                    } else if(key.equals(ACC_KEY)) {
                        accelerometerEnabled = sharedPreferences.getBoolean(ACC_KEY, false);
                        if(accelerometerEnabled) {
                            speedSensor.register(sensorManager);
                        } else {
                            speedSensor.unregister(sensorManager);
                        }
                    }
                }
            };

    private LocationDataProvider locationDataProvider = new LocationDataProvider() {
        @Override
        public void onNewData(Location location) {
            locationData.addData(location);
        }
    };

    private SpeedData speedData = new SpeedData(2000);

    private double accx = 0, accy = 0, accz = 0;
    private int accCounter = 0;
    private int accLimit = 300;

    private SpeedSensor speedSensor = new SpeedSensor(false) {
        @Override
        public void onNewSpeedData(double x, double y, double z) {
            // Sensor spams much more data than we need.
            // So we accumulate it in chunks.
            if(accCounter < accLimit) {
                accx+=x;
                accy+=y;
                accz+=z;
                accCounter++;
                if(accCounter == accLimit) {
                    long time = System.currentTimeMillis();
                    speedData.addData(accx, accy, accz, time);
                    accx = accy = accz = 0;
                    accCounter = 0;
                }
            }
        }

        @Override
        public void onNewRotationAngles(double a, double b, double c) {
        }
    };

    private LocationManager locationManager;
    private SensorManager sensorManager;

    public SpeedObserver(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        accelerometerEnabled = sharedPreferences.getBoolean("use_accelerometer", false);

    }

    public void register(Context context) {
        locationData = new LocationData(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accuracyString = sharedPreferences.getString(GPS_KEY, "high");
        LocationAccuracy accuracy = LocationAccuracy.ACCURACY_MAP.get(accuracyString);

        locationDataProvider.register(locationManager, accuracy);
        if(accelerometerEnabled) {
            speedSensor.register(sensorManager);
        }
    }

    public void unregister(Context context) {
        locationDataProvider.unregister(locationManager);
        if(accelerometerEnabled) {
            speedSensor.unregister(sensorManager);
        }
    }

    public boolean speedAvailable() {
        return locationData.hasFreshData(FRESH_GPS_TIME);
    };

    public float getCurrentSpeed() {
        float speed;
        if(accelerometerEnabled && speedData.hasDataForTime(locationData.getTime())) {
            try {
                float[] speedVector1 = speedData.getDataForTime(locationData.getTime());
                float[] speedVector2 = locationData.getSpeedVector();
                float[] speedVector = new float[]{speedVector1[0] + speedVector2[0],
                                                    speedVector1[1] + speedVector2[1]};
                speed = (float) Math.sqrt(speedVector[0]*speedVector[0] +
                        speedVector[1]*speedVector[1]);
            } catch (Exception e) {
                speed = locationData.getSpeed();;
                e.printStackTrace();
            }
        } else {
            speed = locationData.getSpeed();
        }
        return speed;
    };

}
