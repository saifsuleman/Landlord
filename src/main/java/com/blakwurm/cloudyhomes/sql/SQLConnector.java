package com.blakwurm.cloudyhomes.sql;

import com.blakwurm.cloudyhomes.CloudyHomes;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

interface IConnector {
    Connection openConnection() throws SQLException, ClassNotFoundException;
}

public class SQLConnector implements IConnector {
    private IConnector connector;

    public SQLConnector(String host, int port, String db, String user, String password) {
        this.connector = new MySQLConnector(host, port, db, user, password);
    }

    public SQLConnector(String filename) {
        this.connector = new SQLiteConnector(filename);
    }

    @Override
    public synchronized Connection openConnection() throws SQLException, ClassNotFoundException {
        return this.connector.openConnection();
    }
}

class SQLiteConnector implements IConnector {
    private File file;

    public SQLiteConnector(String filename) {
        this.file = new File(CloudyHomes.getInstance().getDataFolder().getAbsolutePath() + File.separator + filename);
        if (!this.file.exists()) {
            try {
                if (!this.file.getParentFile().exists()) {
                    this.file.getParentFile().mkdirs();
                }
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized Connection openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:sqlite:" + this.file.getAbsolutePath();
        return DriverManager.getConnection(url);
    }
}


class MySQLConnector implements IConnector {
    private final String host, db, user, password;
    private final int port;

    public MySQLConnector(String host, int port, String db, String user, String password) {
        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public synchronized Connection openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true",
                this.host, this.port, this.db);
        return DriverManager.getConnection(url, this.user, this.password);
    }
}