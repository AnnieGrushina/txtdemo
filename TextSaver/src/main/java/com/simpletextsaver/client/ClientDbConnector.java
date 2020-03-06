package com.simpletextsaver.client;

import com.j256.ormlite.stmt.QueryBuilder;
import com.simpletextsaver.DbConnector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDbConnector extends DbConnector<ClientMessage> {
    private static final String CLIENT_PREFIX = "Client";

    public ClientDbConnector(String clientID) {
        super(CLIENT_PREFIX + "-" + clientID + "_", ClientMessage.class);
    }

    public void markDelivered(String msgId) throws SQLException {
        ClientMessage message = messagesDao.queryForId(msgId);
        if (message != null) {
            message.setDelivered(true);
            messagesDao.update(message);
        }
    }

    public List<ClientMessage> getUnsent() {
        List<ClientMessage> messages = new ArrayList<>();
        QueryBuilder<ClientMessage, String> statementBuilder = messagesDao.queryBuilder();
        try {
            statementBuilder.where().like(ClientMessage.DELIVERED_FIELD_NAME, false);
            messages = messagesDao.query(statementBuilder.prepare());
        } catch (SQLException e) {
            log.error("Error while selecting unsent messages", e);
        }
        return messages;
    }
}
