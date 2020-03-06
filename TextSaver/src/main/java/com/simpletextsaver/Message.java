package com.simpletextsaver;

import com.j256.ormlite.field.DatabaseField;

import java.sql.Timestamp;

abstract public class Message {

    public String getMsgId() {
        return msgId;
    }

    public String getMsgText() {
        return msgText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    @DatabaseField(id = true)
    private String msgId;
    @DatabaseField
    private String msgText;
    @DatabaseField
    private Timestamp timestamp;

    public Message() {
        // ORMLite needs a no-arg constructor
    }

    public Message(String msgId, String msgText, Timestamp timestamp) {
        this.msgId = msgId;
        this.msgText = msgText;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgId='" + msgId + '\'' +
                ", msgText='" + msgText + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
