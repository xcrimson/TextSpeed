package com.textspeed.app.sensors;

/**
 * Created by crimson on 08.04.2015.
 */
public class SpeedData {

    private final static long MAX_TIME_DIFFERENCE = 1000;

    private double[] xs;
    private double[] ys;
    private double[] zs;

    private long[] times;

    private int caret;
    private int capacity;

    public SpeedData(int capacity) {
        xs = new double[capacity];
        ys = new double[capacity];
        zs = new double[capacity];
        times = new long[capacity];
        caret = 0;
        this.capacity = capacity;
    };

    public void addData(double x, double y, double z, long time) {
        xs[caret] = x;
        ys[caret] = y;
        zs[caret] = z;
        times[caret] = time;
        caret++;
        if(caret >= capacity) {
            caret = 0;
        }
    }

    private int getSafeIndex(int index) {
        return index >= capacity ? index - capacity : index;
    }

    public boolean hasDataForTime(long time) {
        long minTime = times[caret]==0? times[0] : times[caret];
        long maxTime = caret==0? times[capacity-1] : times[caret-1];
        return time > minTime && time <= maxTime;
    };

    private int getTimeIndex(long time) {
        int result = -1;
        for(int i=0; i<capacity; i++) {
            if(times[getSafeIndex(i + caret)] < time &&
                    times[getSafeIndex(i + caret + 1)] >= time) {
                result = getSafeIndex(i + caret);
            }
        }
        return result;
    };

    public float[] getDataForTime(long time) throws Exception {
        if(!hasDataForTime(time)) {
            throw new Exception("No data for time");
        }
        float[] sumSpeed = new float[]{0,0,0};
        for(int i=getTimeIndex(time); i<caret; i++) {
            sumSpeed[0]+=xs[i];
            sumSpeed[1]+=ys[i];
            sumSpeed[2]+=zs[i];
        }

        return sumSpeed;
    };

}
