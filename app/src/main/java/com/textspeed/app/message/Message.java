package com.textspeed.app.message;

import java.util.Comparator;

public class Message {

    private final int id;
    private final boolean isRead;
    private final boolean isIncoming;
    private final String text;
    private final String opponent;
    private final long date;

    public final static Comparator<Message> LAST_ON_TOP = new Comparator<Message>() {
        @Override
        public int compare(Message lhs, Message rhs) {
            int result;
            if(lhs.getDate() == rhs.getDate()) {
                result = 0;
            } else if(lhs.getDate() < rhs.getDate()) {
                result = 1;
            } else {
                result = -1;
            }
            return result;
        }
    };

    Message(int id, boolean isRead, boolean isIncoming, String text,
            String opponent, long date) {
        this.id = id;
        this.isRead = isRead;
        this.isIncoming = isIncoming;
        this.text = text;
        this.opponent = opponent;
        this.date = date;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public boolean isOutgoing() {
        return !isIncoming;
    }

    public String getText() {
        return text;
    }

    public String getOpponent() {
        return opponent;
    }

    public long getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

}
