package net.saifs.landlord;

import net.saifs.landlord.command.*;
import net.saifs.landlord.config.Config;
import net.saifs.landlord.handler.HomesManager;
import net.saifs.landlord.sql.SQLConnector;
import net.saifs.landlord.utils.LocaleManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PLAYERS DATABASE
 * ==================================
 * Player (UUID), Homecount (integer)
 * <p>
 * HOMES DATABASE
 * ===============================================================================================
 * Player (UUID), X (double), Y (double), Z (double), World (String), Pitch (double), Yaw (double)
 */
public final class Landlord extends JavaPlugin {
    private static Landlord instance;
    private SQLConnector connector;
    private HomesManager homesManager;
    private Config config;
    private LocaleManager localeManager;

    public static Landlord getInstance() {
        return instance;
    }

    public static HomesManager getHomesManager() {
        return getInstance().homesManager;
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
        this.homesManager = new HomesManager(connector);
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

        this.connector = mysql ? new SQLConnector(config.getConfig().getString("mysql.host"), config.getConfig().getInt("mysql.port"),
                config.getConfig().getString("mysql.database"), config.getConfig().getString("mysql.username"), config.getConfig().getString("mysql.password")) : new SQLConnector("homes.db");
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
