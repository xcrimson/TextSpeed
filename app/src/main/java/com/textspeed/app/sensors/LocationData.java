package com.textspeed.app.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Created by crimson on 05.04.2015.
 */
public class LocationData {

    private final static String TIME_POSTFIX = "_time";
    private final static String LATITUDE_POSTFIX = "_latitude";
    private final static String LONGITUDE_POSTFIX = "_longitude";

    private final static String A = "locationA";
    private final static String B = "locationB";

    private Location locationA;
    private Location locationB;

    private SharedPreferences preferences;

    public LocationData(Context context) {
        preferences = context.getSharedPreferences("textspeed", Context.MODE_PRIVATE);
        locationA = getLocation(A);
        locationB = getLocation(B);
    }

    private void storeLocation(Location location, String prefix) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(prefix + TIME_POSTFIX, location.getTime());
        editor.putLong(prefix + LATITUDE_POSTFIX, Double.doubleToRawLongBits(location.getLatitude()));
        editor.putLong(prefix + LONGITUDE_POSTFIX, Double.doubleToRawLongBits(location.getLongitude()));
        editor.commit();
    }

    private Location getLocation(String prefix) {
        Location location = new Location("prefs");
        long time = preferences.getLong(prefix + TIME_POSTFIX, -1);
        double latitude = Double.longBitsToDouble(preferences.getLong(prefix + LATITUDE_POSTFIX, 0));
        double longitude = Double.longBitsToDouble(preferences.getLong(prefix + LONGITUDE_POSTFIX, 0));
        location.setTime(time);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    };

    public boolean hasFreshData(long freshnessLimit) {
        return locationA.getTime()!=-1 && locationB.getTime()!=-1 &&
                getTime() > System.currentTimeMillis() - freshnessLimit;
    };

    public void addData(Location location) {

        if(locationA.getTime() < locationB.getTime()) {
            locationA = location;
            storeLocation(location, A);
        } else {
            locationB = location;
            storeLocation(location, B);
        }

    }

    public float getSpeed() {
        float speed;
        if(locationB.equals(locationA)) {
            speed = 0f;
        } else {
            float distance = locationA.distanceTo(locationB); // meters
            float time = Math.abs(locationA.getTime() - locationB.getTime()) / 1000f; // seconds
            speed = distance / time;
        }
        return speed;
    };

    public float[] getSpeedVector() {
        float[] speed = new float[]{0,0};
        if(!locationB.equals(locationA)) {
            boolean aFirst = locationA.getTime() < locationB.getTime();
            Location start = aFirst? locationA : locationB;
            Location end = aFirst? locationB : locationA;

            float[] distanceY = new float[1];
            float[] distanceX = new float[1];

            Location.distanceBetween(
                    start.getLatitude(), start.getLongitude(),
                    end.getLatitude(), start.getLongitude(), distanceY);

            Location.distanceBetween(
                    start.getLatitude(), start.getLongitude(),
                    start.getLatitude(), end.getLongitude(), distanceX);

            float time = Math.abs(locationA.getTime() - locationB.getTime()) / 1000f; // seconds

            float bearing = start.bearingTo(end);

            boolean signX = bearing <= 180;
            boolean signY = bearing <=90 || bearing > 270;

            float speedX = (signX? 1 : -1) * distanceX[0] / time;
            float speedY = (signY? 1 : -1) * distanceY[0] / time;

            speed[0] = speedX;
            speed[1] = speedY;
        }
        return speed;
    }

    public long getTime() {
        return (locationA.getTime() + locationB.getTime()) / 2;
    }

}
