package com.simpletextsaver.server;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.simpletextsaver.client.ClientMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static spark.Spark.*;

public class TextSaverServer {
    private static final Logger log = LoggerFactory.getLogger(TextSaverServer.class);
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int INVALID_INPUT = 400;

    private void start() {
        ServerDbConnector dbConnector = new ServerDbConnector();

        // Instances of Genson are immutable and thread safe, you should reuse them.
        Genson genson = new Genson();

        post("/hello", (req, res) ->
                {
                    // Header set by client or proxy ip.
                    String sender = req.headers(ClientMessageSender.CLIENT_ID_HEADER);
                    if (sender == null) {
                        sender = req.ip();
                    }

                    List<ServerMessage> messages = null;
                    try {
                        messages = genson.deserialize(req.body(), new GenericType<List<ServerMessage>>() {
                        });
                    } catch (com.owlike.genson.JsonBindingException e) {
                        log.error("Invalid json request body from " + sender, e.toString());
                    }

                    if (messages != null) {
                        try {
                            for (ServerMessage message : messages) {
                                if (message != null && message.isValid()) {
                                    message.setSenderAddress(sender);
                                    if(dbConnector.addMessage(message)) {
                                        log.info("New message from: " + sender + " saved in DB: " + message.getMsgId());
                                    }
                                }
                                else
                                {
                                    res.status(INVALID_INPUT);
                                    log.info("Invalid message from: " + sender + ": " + message);
                                }
                            }
                        } catch (SQLException e) {
                            res.status(INTERNAL_SERVER_ERROR);
                            log.error(messages.size() + " message(s) from " + sender + " not saved in DB.", e);
                        }
                    } else {
                        res.status(INVALID_INPUT);
                    }

                    dbConnector.logStatus();
                    return "";
                }
        );
    }

    public static void main(String[] args) {
        new TextSaverServer().start();
    }
}