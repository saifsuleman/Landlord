package com.blakwurm.cloudyhomes.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnector {
    private String host, db, user, password;
    private int port;

    public SQLConnector(String host, int port, String db, String user, String password) {
        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public Connection openConnection() throws SQLException, ClassNotFoundException {
        Connection connection;
        synchronized (this) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s",
                    this.host, this.port, this.db), this.user, this.password);
        };
        return connection;
    }
}
