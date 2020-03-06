package com.simpletextsaver;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.simpletextsaver.server.TextSaverServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.sql.SQLException;

public class DbConnector<T extends Message> {
    private static final String DATABASE_FILENAME = "Messages.db";
    private static final String DB_PROTOCOL_PREFIX = "jdbc:sqlite:";
    private static final Cleaner CLEANER = Cleaner.create();
    protected static final Logger log = LoggerFactory.getLogger(DbConnector.class);

    private ConnectionSource connectionSource;
    protected Dao<T, String> messagesDao;

    public DbConnector(String prefix, Class messageClass) {
        CLEANER.register(this, () -> {
            if (connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (IOException e) {
                    log.error("Closing DB connection", e);
                }
            }
        });

        try {
            String databaseUrl = DB_PROTOCOL_PREFIX + prefix + DATABASE_FILENAME;
            connectionSource =
                    new JdbcConnectionSource(databaseUrl);
            messagesDao =
                    DaoManager.createDao(connectionSource, messageClass);

            TableUtils.createTableIfNotExists(connectionSource, messageClass);
        } catch (SQLException e) {
            throw new RuntimeException("DB can not be created or connected", e);
        }
    }

    public boolean containsMessage(String msgId) {
        try {
            return messagesDao.queryForId(msgId) != null;
        } catch (SQLException e) {
            log.error("Unable to get message from DB", e);
        }
        return false;
    }

    public boolean addMessage(T msg) throws SQLException {
        if (containsMessage(msg.getMsgId())) {
            return false;
        }
        messagesDao.create(msg);
        return true;
    }

    public void logStatus() {
        try {
            log.info("" + messagesDao.countOf() + " messages in DB");
        } catch (SQLException e) {
            log.error("Can not list messages", e);
        }
    }
}
