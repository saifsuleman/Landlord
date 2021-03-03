package com.blakwurm.cloudyhomes.sql;

import com.blakwurm.cloudyhomes.CloudyHomes;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector implements IConnector {
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
