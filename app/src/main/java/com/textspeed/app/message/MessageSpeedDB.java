package com.textspeed.app.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by crimson on 04.04.2015.
 */
public class MessageSpeedDB {

    private final static String MESSAGES_TABLE_NAME = "MessagesTable";
    private final static String MESSAGES_ID_KEY = "_id";
    private final static String MESSAGES_SPEED_KEY = "speed";

    private DBHelper dbHelper;
    private final Object dbLock = new Object();

    public void initiate(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void storeMessageSpeed(int id, float speed) throws SQLiteException {
        if(dbHelper==null) {
            throw new SQLiteException("Helper is not initialized. Use initialize() " +
                    "function before making this call.");
        }
        synchronized (dbLock) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues messageSpeed = new ContentValues();
            messageSpeed.put(MESSAGES_ID_KEY, id);
            messageSpeed.put(MESSAGES_SPEED_KEY, speed);
            long rowId = db.replace(MESSAGES_TABLE_NAME, null, messageSpeed);
            if (rowId == -1) {
                throw new SQLiteException("DB insertion failed");
            }
            db.close();
        }
    }

    public Map<Integer, Float> getMessageSpeeds() throws SQLiteException {
        if(dbHelper==null) {
            throw new SQLiteException("Helper is not initialized. Use initialize() " +
                    "function before making this call.");
        }
        synchronized (dbLock) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Map<Integer, Float> result = new HashMap<Integer, Float>();
            String[] columns = new String[]{MESSAGES_ID_KEY, MESSAGES_SPEED_KEY};
            Cursor cursor = db.query(MESSAGES_TABLE_NAME, columns, null, null, null, null, null);
            if (cursor == null) {
                throw new SQLiteException("Null cursor to DB");
            }
            int idIndex = cursor.getColumnIndex(MESSAGES_ID_KEY);
            int speedIndex = cursor.getColumnIndex(MESSAGES_SPEED_KEY);
            if (idIndex == -1 || speedIndex == -1) {
                throw new SQLiteException("No column with such index exist");
            }
            int id;
            float speed;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    id = cursor.getInt(idIndex);
                    speed = cursor.getFloat(speedIndex);
                    result.put(id, speed);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            db.close();
            return result;
        }
    };

    public class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "db.db";
        private static final int DATABASE_VERSION = 1;

        private static final String SMS_FLAGS_TABLE_CREATE =
                "create table " +
                        MESSAGES_TABLE_NAME +
                        " (" +
                        MESSAGES_ID_KEY + " integer primary key, " +
                        MESSAGES_SPEED_KEY + " real"+
                        ");";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(SMS_FLAGS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database,
                              int oldVersion, int newVersion) {
        }

    }

}
