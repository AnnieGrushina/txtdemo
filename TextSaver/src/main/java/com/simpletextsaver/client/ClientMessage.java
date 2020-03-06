package com.simpletextsaver.client;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.simpletextsaver.Message;

import java.sql.Timestamp;

@DatabaseTable(tableName = "messages")
public class ClientMessage extends Message {
    public static final String DELIVERED_FIELD_NAME = "delivered";

    @DatabaseField(columnName = DELIVERED_FIELD_NAME)
    private boolean delivered;

    public ClientMessage() {
    }

    public ClientMessage(String msgId, String msgText, Timestamp timestamp) {
        super(msgId, msgText, timestamp);
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}
