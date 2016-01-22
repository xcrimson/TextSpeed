package com.textspeed.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.textspeed.app.message.Message;
import com.textspeed.app.message.MessageObserver;
import com.textspeed.app.message.MessageSpeedDB;
import com.textspeed.app.sensors.LocationAccuracy;

import java.util.Map;

/**
 * Created by crimson on 04.04.2015.
 */
public class TextSpeedService extends Service {

    private static TextSpeedService service;

    private MessageSpeedDB messageSpeedDB = new MessageSpeedDB();
    private SpeedObserver speedObserver;

    private static ServiceReadyListener serviceReadyListener;

    private MessageObserver messageSentObserver = new MessageObserver(
            new Handler(Looper.getMainLooper()), getContext()) {
        @Override
        public void onMessage(Message message) {
            if(message.isOutgoing() && speedObserver.speedAvailable()) {
                messageSpeedDB.storeMessageSpeed(message.getId(),
                        speedObserver.getCurrentSpeed());
            }
        }
    };

    public static Map<Integer, Float> getMessageSpeeds() throws Exception {
        if(service == null) {
            throw new Exception("You need to start text speed service before using it");
        }
        return service.messageSpeedDB.getMessageSpeeds();
    };

    public static void setServiceReadyListener(ServiceReadyListener listener) {
        serviceReadyListener = listener;
        if(service!=null){
            serviceReadyListener.onServiceReady();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        speedObserver = new SpeedObserver(this);
        messageSpeedDB.initiate(this);
        messageSentObserver.register(this);
        speedObserver.register(this);
        service = this;
        if(serviceReadyListener!=null) {
            serviceReadyListener.onServiceReady();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageSentObserver.unregister(this);
        speedObserver.unregister(this);
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Context getContext() {
        return this;
    };

}
