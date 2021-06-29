package net.saifs.landlord.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnector implements IConnector {
    private IConnector connector;

    public SQLConnector(String host, int port, String db, String user, String password) {
        this.connector = new MySQLConnector(host, port, db, user, password);
    }

    public SQLConnector(String filename) {
        this.connector = new SQLiteConnector(filename);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connector.getConnection();
    }
}


