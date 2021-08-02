package net.saifs.landlord.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnector {
    Connection getConnection() throws SQLException;
}
