package com.textspeed.app.sensors;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class LocationDataProvider {

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            onNewData(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    private LocationAccuracy accuracy;
    private Criteria locationCriteria = new Criteria();
    private String precision;

    public LocationDataProvider() {
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationCriteria.setAltitudeRequired(false);
        locationCriteria.setBearingRequired(false);
    }

    public void register(LocationManager locationManager, LocationAccuracy accuracy) {
        this.accuracy = accuracy;
        locationManager.removeUpdates(locationListener);
        String provider = locationManager.getBestProvider(locationCriteria, true);
        locationManager.requestLocationUpdates(provider,
                accuracy.getTime(),
                accuracy.getDistance(),
                locationListener);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null) {
            onNewData(location);
        }
    }

    public void changePrecision(LocationManager locationManager, LocationAccuracy accuracy) {
        unregister(locationManager);
        register(locationManager, accuracy);
    }

    public void unregister(LocationManager locationManager) {
        locationManager.removeUpdates(locationListener);
    }

    public abstract void onNewData(Location location);

    public void setPrecision(LocationManager locationManager, String precision) {
        if(LocationAccuracy.ACCURACY_MAP.containsKey(precision)) {
            changePrecision(locationManager, LocationAccuracy.ACCURACY_MAP.get(precision));
        }
    }
}
