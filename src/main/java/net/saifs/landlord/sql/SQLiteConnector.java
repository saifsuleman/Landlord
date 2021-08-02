package net.saifs.landlord.sql;

import net.saifs.landlord.Landlord;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector implements IConnector {
    private final File file;
    private Connection connection;

    public SQLiteConnector(String filename) {
        this.file = new File(Landlord.getInstance().getDataFolder().getAbsolutePath() + File.separator + filename);
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
        try {
            this.connection = this.openConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed())
                this.connection = openConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    public Connection openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:sqlite:" + this.file.getAbsolutePath();
        return DriverManager.getConnection(url);
    }
}