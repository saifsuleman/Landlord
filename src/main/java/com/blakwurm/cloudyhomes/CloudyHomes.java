package com.blakwurm.cloudyhomes;

import com.blakwurm.cloudyhomes.command.*;
import com.blakwurm.cloudyhomes.config.Config;
import com.blakwurm.cloudyhomes.handler.HomesManager;
import com.blakwurm.cloudyhomes.sql.SQLConnector;
import com.blakwurm.cloudyhomes.utils.LocaleManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * PLAYERS DATABASE
 * ==================================
 * Player (UUID), Homecount (integer)
 * <p>
 * HOMES DATABASE
 * ===============================================================================================
 * Player (UUID), X (double), Y (double), Z (double), World (String), Pitch (double), Yaw (double)
 */
public final class CloudyHomes extends JavaPlugin {
    private static CloudyHomes instance;
    private Connection connection;
    private HomesManager homesManager;
    private Config config;
    private LocaleManager localeManager;

    public static CloudyHomes getInstance() {
        return instance;
    }

    public static HomesManager getHomesManager() {
        return getInstance().homesManager;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Plugin startup logic
        this.config = new Config("config.yml");
        if (!this.initializeSQL()) {
            return;
        }
        this.homesManager = new HomesManager(getConnection());
        this.localeManager = new LocaleManager();

        registerCommand("home", new HomeCommand());
        registerCommand("playerhome", new PlayerHomeCommand());
        registerCommand("sethome", new SetHomeCommand());
        registerCommand("listhomes", new HomesCommand());
        registerCommand("deletehome", new DeleteHomeCommand());
        registerCommand("modifyhomecount", new ModifyHomecountCommand());
    }

    private boolean initializeSQL() {
        boolean mysql = config.getConfig().getBoolean("mysql.enabled");

        SQLConnector connector = mysql ? new SQLConnector(config.getConfig().getString("mysql.host"), config.getConfig().getInt("mysql.port"),
                config.getConfig().getString("mysql.database"), config.getConfig().getString("mysql.username"), config.getConfig().getString("mysql.password")) : new SQLConnector("homes.db");
        try {
            this.connection = connector.openConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand pluginCommand = getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(executor);
            if (executor instanceof TabCompleter) {
                pluginCommand.setTabCompleter((TabCompleter) executor);
            }
        }
    }

    public Config getPluginConfig() {
        return config;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
