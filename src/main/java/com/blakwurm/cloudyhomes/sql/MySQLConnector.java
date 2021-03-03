package com.blakwurm.cloudyhomes.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector implements IConnector {
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
