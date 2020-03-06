package com.simpletextsaver.client;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Stream;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TextSaverClient {
    private static final Lorem lorem = LoremIpsum.getInstance(); // text generator
    private static final Logger log = LoggerFactory.getLogger(TextSaverClient.class);
    private static final long MESSAGE_INTERVAL = 3000;

    private ClientDbConnector connector;

    public TextSaverClient(ClientDbConnector connector) {
        this.connector = connector;
    }

    void generateTextFlow() {

        Stream.generate(() -> UUID.randomUUID()).forEach((msgId) -> {
            try {
                connector.addMessage(new ClientMessage(msgId.toString(),
                        lorem.getWords(5, 10),
                        new Timestamp(System.currentTimeMillis())));
                log.info("New message generated: " + msgId.toString());
            } catch (SQLException e) {
                log.error("Message will be skipped because of error", e);
            }
            try {
                Thread.sleep((long) (Math.random() * MESSAGE_INTERVAL));
            } catch (InterruptedException e) {
                log.error("Pause in interrupted", e);
            }
        });
    }

    public static void main(String[] args) throws UnknownHostException {
        String clientID = InetAddress.getLocalHost().getHostName();
        ClientDbConnector dbConnector = new ClientDbConnector(clientID);

        // A single dbConnector is used to work with DAO
        // ORMLite is threadsafe in case the underlying DB is thread safe
        // SQLite is also thread safe for common builds
        new Thread(new ClientMessageSender(dbConnector, clientID)).start();
        new TextSaverClient(dbConnector).generateTextFlow();

    }
}
