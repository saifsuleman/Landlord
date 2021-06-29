package net.saifs.landlord.sql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector implements IConnector {
    private HikariDataSource hikari;

    public MySQLConnector(String host, int port, String db, String user, String password) {
        this.hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", db);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.hikari.getConnection();
    }
}
