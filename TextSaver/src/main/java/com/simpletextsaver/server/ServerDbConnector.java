package com.simpletextsaver.server;

import com.j256.ormlite.dao.Dao;
import com.simpletextsaver.DbConnector;
import com.simpletextsaver.Message;
import com.simpletextsaver.server.ServerMessage;

public class ServerDbConnector extends DbConnector<ServerMessage> {
    private static final String SERVER_PREFIX = "Server";

    private Dao<Message, String> messagesDao;

    public ServerDbConnector() {
        super(SERVER_PREFIX, ServerMessage.class);
    }
}
