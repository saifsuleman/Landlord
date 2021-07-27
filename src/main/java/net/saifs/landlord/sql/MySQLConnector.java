package net.saifs.landlord.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MySQLConnector implements IConnector {
    private final HikariDataSource hikari;

    public MySQLConnector(String ip, int port, String username, String password, String database) {
        this("jdbc:mysql://" + ip + ":" + port + "/" + database, username, password);
    }

    public MySQLConnector(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cacheCallableStmts", true);
        config.addDataSourceProperty("alwaysSendSetIsolation", false);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("maxLifetime", TimeUnit.MINUTES.toMillis(10));
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15));
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(10));
        this.hikari = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.hikari.getConnection();
    }
}
