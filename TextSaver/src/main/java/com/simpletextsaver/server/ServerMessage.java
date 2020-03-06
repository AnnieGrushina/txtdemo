package com.simpletextsaver.server;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.simpletextsaver.Message;

import java.sql.Timestamp;

@DatabaseTable(tableName = "messages")
public class ServerMessage extends Message {
    @DatabaseField
    private String senderAddress;

    public ServerMessage() {
    }

    public ServerMessage(String msgId, String msgText, String senderAddress, Timestamp timestamp) {
        super(msgId, msgText, timestamp);
        setSenderAddress(senderAddress);
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public boolean isValid() {
        return getMsgId() != null && getMsgText() != null;
    }
}
