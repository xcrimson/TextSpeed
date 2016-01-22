package com.textspeed.app.message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.telephony.SmsManager;

/**
 * Created by crimson on 04.04.2015.
 */
public abstract class MessageObserver extends ContentObserver {

    private final Context context;

    public MessageObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    public void register(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, this);
    }

    public void unregister(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.unregisterContentObserver(this);
    }

    @Override
    public void onChange (boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        try {
            Message message = MessageUtil.getMessageWithUri(uri, context);
            onMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onMessage(Message message);

}
