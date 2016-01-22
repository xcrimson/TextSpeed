package com.textspeed.app.message;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crimson on 04.04.2015.
 */
public class MessageUtil {

    private final static Uri MESSAGES_URI = Uri.parse("content://sms/");

    public static List<Message> loadMessages(Context context) {
        List<Message> messages = new ArrayList<Message>();

        Cursor c = context.getContentResolver().query(MESSAGES_URI, null, null ,null,null);

        try {
            if(c!=null) {

                int id;
                boolean isRead;
                boolean isIncoming;
                String text;
                String opponent;
                long date;

                String isReadValue;
                String isIncomingValue;
                final int addressColumn = c.getColumnIndexOrThrow("address");
                final int isReadColumn = c.getColumnIndexOrThrow("read");
                final int typeColumn = c.getColumnIndexOrThrow("type");
                final int bodyColumn = c.getColumnIndexOrThrow("body");
                final int dateColumn = c.getColumnIndexOrThrow("date");
                final int idColumn = c.getColumnIndexOrThrow("_id");

                if(c.moveToFirst()){

                    for(int i=0;i<c.getCount();i++){
                        opponent = c.getString(addressColumn);

                        id = Integer.parseInt(c.getString(idColumn).toString());

                        isReadValue = c.getString(isReadColumn);
                        isRead = isReadValue!=null && isReadValue.equals("1");
                        isIncomingValue = c.getString(typeColumn);
                        isIncoming = isIncomingValue!=null && isIncomingValue.equals("1");
                        text = c.getString(bodyColumn);
                        date = Long.parseLong(c.getString(dateColumn).toString());

                        messages.add(new Message(id, isRead, isIncoming, text, opponent, date));

                        c.moveToNext();
                    }
                }

            }
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e2) {
        } finally {
            if(c!=null) {
                c.close();
            }
        }

        return messages;
    }

    public static Message getMessageWithUri(Uri uri, Context context) throws Exception {
        String idString = uri.toString().replaceAll("\\D+","");
        if(idString.length()==0) {
            throw new Exception("Message Uri does not contain message Id");
        }
        int id = Integer.parseInt(idString);
        return getMessageWithUri(id, context);
    };

    public static Message getMessageWithUri(int messageId, Context context) {
        Message result = null;

        Cursor c = context.getContentResolver().query(MESSAGES_URI, null, null ,null,null);

        try {
            if(c!=null) {

                int id;
                boolean isRead;
                boolean isIncoming;
                String text;
                String opponent;
                long date;

                String isReadValue;
                String isIncomingValue;
                final int addressColumn = c.getColumnIndexOrThrow("address");
                final int isReadColumn = c.getColumnIndexOrThrow("read");
                final int typeColumn = c.getColumnIndexOrThrow("type");
                final int bodyColumn = c.getColumnIndexOrThrow("body");
                final int dateColumn = c.getColumnIndexOrThrow("date");
                final int idColumn = c.getColumnIndexOrThrow("_id");

                if(c.moveToFirst()){

                    for(int i=0;i<c.getCount();i++){

                        id = c.getInt(idColumn);

                        if(id == messageId) {
                            opponent = c.getString(addressColumn);
                            isReadValue = c.getString(isReadColumn);
                            isRead = isReadValue != null && isReadValue.equals("1");
                            isIncomingValue = c.getString(typeColumn);
                            isIncoming = isIncomingValue != null && isIncomingValue.equals("1");
                            text = c.getString(bodyColumn);
                            date = Long.parseLong(c.getString(dateColumn).toString());
                            result = new Message(id, isRead, isIncoming, text, opponent, date);
                            break;
                        }

                        c.moveToNext();
                    }
                }

            }
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e2) {
        } finally {
            if(c!=null) {
                c.close();
            }
        }

        return result;
    };

}
