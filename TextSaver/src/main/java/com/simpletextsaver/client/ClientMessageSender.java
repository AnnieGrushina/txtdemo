package com.simpletextsaver.client;

import com.owlike.genson.Genson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.core.DB;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

public class ClientMessageSender implements Runnable {
    public static final String CLIENT_ID_HEADER = "ClientID";

    private static final int RESPONSE_STATUS_CODE_OK = 200;
    private static final String SERVER_URI = "http://" + System.getenv("SERVER_URL") + "/hello";
    private static final String REQUEST_HEADER_TYPE = "Content-Type";
    private static final String REQUEST_HEADER_VAL = "application/json";
    private static final Logger log = LoggerFactory.getLogger(ClientMessageSender.class);
    private static final long DB_REQUEST_PERIOD = 2000;

    private ClientDbConnector connector;
    private String clientID;

    public ClientMessageSender(ClientDbConnector connector, String clientID) {
        this.connector = connector;
        this.clientID = clientID;
    }

    public void run() {
        Genson genson = new Genson();

        while (true) {
            List<ClientMessage> msgList = connector.getUnsent();

            if (msgList.size() > 0) {
                log.info("Sending " + msgList.size() + " undelivered messages to " + SERVER_URI);
            }

            try {
                // Need it here for toxiproxy. Otherwise client reuses the first successful connection.
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofMillis(DB_REQUEST_PERIOD))
                        .build();

                HttpResponse<Stream<String>> response = client.send(HttpRequest.newBuilder()
                                .uri(URI.create(SERVER_URI))
                                .timeout(Duration.ofMillis(DB_REQUEST_PERIOD))
                                .header(REQUEST_HEADER_TYPE, REQUEST_HEADER_VAL)
                                .header(CLIENT_ID_HEADER, clientID)
                                // Sends ClientMessage. Normally we need Message here.
                                .POST(HttpRequest.BodyPublishers.ofString(genson.serialize(msgList)))
                                .build(),
                        HttpResponse.BodyHandlers.ofLines());

                if (response != null && response.statusCode() == RESPONSE_STATUS_CODE_OK) {
                    for (ClientMessage msg : msgList) {
                        connector.markDelivered(msg.getMsgId());
                    }
                    log.info("Confirmed.");
                } else {
                    throw new IOException("Response is not ok: " + response);
                }
            } catch (IOException | InterruptedException | SQLException e) {
                log.info("Delivery was not confirmed for " + msgList.size() + " messages");
            }

            connector.logStatus();

            try {
                Thread.sleep(DB_REQUEST_PERIOD);
            } catch (InterruptedException e) {
                log.error("Sender thread was interrupted");
            }
        }
    }
}
