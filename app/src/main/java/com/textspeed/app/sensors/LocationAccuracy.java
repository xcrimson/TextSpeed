package com.textspeed.app.sensors;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by crimson on 05.04.2015.
 */
public class LocationAccuracy {

    public static final LocationAccuracy ULTRA_ACCURACY =
            new LocationAccuracy(0, 0);
    public static final LocationAccuracy HIGH_ACCURACY =
            new LocationAccuracy(10_000, 10);
    public static final LocationAccuracy MEDIUM_ACCURACY =
            new LocationAccuracy(60_000, 100);
    public static final LocationAccuracy LOW_ACCURACY =
            new LocationAccuracy(120_000, 500);

    public final static Map<String, LocationAccuracy> ACCURACY_MAP;
    static {
        Map<String, LocationAccuracy> map = new HashMap<String, LocationAccuracy>();
        map.put("low", LOW_ACCURACY);
        map.put("medium", MEDIUM_ACCURACY);
        map.put("high", HIGH_ACCURACY);
        map.put("ultra", ULTRA_ACCURACY);
        ACCURACY_MAP = map;
    }

    private int time; //milliseconds
    private int distance; //meters

    public LocationAccuracy(int time, int distance) {
        this.time = time;
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public int getDistance() {
        return distance;
    }

}
