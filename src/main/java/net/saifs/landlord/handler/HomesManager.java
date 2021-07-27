package net.saifs.landlord.handler;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.saifs.landlord.Home;
import net.saifs.landlord.Landlord;
import net.saifs.landlord.sql.SQLConnector;
import net.saifs.landlord.utils.LocaleManager;
import net.saifs.landlord.utils.concurrency.Promise;
import net.saifs.landlord.utils.concurrency.ThreadUtil;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class HomesManager {
    private SQLConnector connector;
    private List<Home> homes;
    private Map<UUID, Integer> allowedHomes;

    public HomesManager(SQLConnector connector) {
        this.connector = connector;
        createTables();
        loadHomes().then(homes -> this.homes = homes);
        loadAllowedHomes().then(allowedHomes -> this.allowedHomes = allowedHomes);
    }

    public List<Home> getHomes(OfflinePlayer player) {
        return homes.stream().filter(home -> home.getOwner().equals(player)).collect(Collectors.toList());
    }

    private void createTables() {
        ThreadUtil.async(() -> {
            try (Connection connection = connector.getConnection()) {
                String query = Landlord.getInstance().getPluginConfig().getConfig().getBoolean("mysql.enabled") ?
                        "CREATE TABLE IF NOT EXISTS homes (homename VARCHAR(255) NOT NULL PRIMARY KEY, owner VARCHAR(255) NOT NULL, x FLOAT(25) NOT NULL, y "
                                + "FLOAT(25) NOT NULL, z FLOAT(25) NOT NULL, yaw FLOAT(10) NOT NULL, pitch FLOAT(10) NOT NULL, world VARCHAR(255) NOT NULL)" :
                        "CREATE TABLE IF NOT EXISTS homes (homename TEXT(255) NOT NULL PRIMARY KEY, owner TEXT(255) NOT NULL, " +
                                "x REAL(25) NOT NULL, y REAL(25) NOT NULL, z REAL(25) NOT NULL, yaw REAL(10) NOT NULL, pitch REAL(10) NOT NULL, world TEXT(255) NOT NULL)";
                connection.prepareStatement(query).executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (Connection connection = connector.getConnection()) {
                String query = "CREATE TABLE IF NOT EXISTS homeplayers (uuid VARCHAR(255) PRIMARY KEY, allowedhomecount INT(10) NOT NULL)";
                connection.prepareStatement(query).executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Promise<List<Home>> loadHomes() {
        return new Promise<>(() -> {
            List<Home> homes = new ArrayList<>();

            try (Connection connection = connector.getConnection()) {
                String query = "SELECT * FROM homes";
                ResultSet results = connection.prepareStatement(query).executeQuery();

                while (results.next()) {
                    OfflinePlayer owner = Landlord.getInstance().getServer().getOfflinePlayer(UUID.fromString(results.getString("owner")));
                    String name = results.getString("homename");
                    double x = results.getDouble("x");
                    double y = results.getDouble("y");
                    double z = results.getDouble("z");
                    float yaw = results.getFloat("yaw");
                    float pitch = results.getFloat("pitch");

                    World world = Landlord.getInstance().getServer().getWorld(results.getString("world"));
                    if (world == null) continue;

                    Location loc = new Location(world, x, y, z, yaw, pitch);
                    Home home = new Home(name, owner, loc);
                    homes.add(home);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return homes;
        });
    }

    public void addHome(Home home) {
        // Deletes home if exists - this ensures one set-home will override another.
        this.homes.add(home);

        ThreadUtil.async(() -> {
            Location loc = home.getLocation();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            float yaw = loc.getYaw();
            float pitch = loc.getPitch();
            World world = home.getLocation().getWorld();
            if (world == null) return;
            try (Connection connection = connector.getConnection()) {
                String query = "INSERT INTO homes (homename, owner, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE owner = VALUES(owner), x = VALUES(x), y = VALUES(y), " +
                        "z = VALUES(z), yaw = VALUES(yaw), pitch = VALUES(pitch), world = VALUES(world)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, home.getName());
                statement.setString(2, home.getOwner().getUniqueId().toString());
                statement.setDouble(3, x);
                statement.setDouble(4, y);
                statement.setDouble(5, z);
                statement.setFloat(6, yaw);
                statement.setFloat(7, pitch);
                statement.setString(8, world.getName());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeHome(Home home) {
        this.homes.remove(home);

        ThreadUtil.async(() -> {
            try (Connection connection = connector.getConnection()) {
                String query = "DELETE FROM homes WHERE homename=? AND owner=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, home.getName());
                statement.setString(2, home.getOwner().getUniqueId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getHomeNames(OfflinePlayer player) {
        return getHomes(player).stream().map(Home::getName).collect(Collectors.toList());
    }

    public Promise<Map<UUID, Integer>> loadAllowedHomes() {
        return new Promise<>(() -> {
            Map<UUID, Integer> map = new HashMap<>();

            try (Connection connection = connector.getConnection()) {
                String query = "SELECT * FROM homeplayers";
                ResultSet results = connection.prepareStatement(query).executeQuery();

                while (results.next()) {
                    UUID uuid = UUID.fromString(results.getString("uuid"));
                    int allowed = results.getInt("allowedhomecount");
                    map.put(uuid, allowed);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        });
    }

    public void setAllowedHomesCount(UUID uuid, int count) {
        allowedHomes.put(uuid, count);

        ThreadUtil.async(() -> {
            try (Connection connection = connector.getConnection()) {
                String query = "INSERT INTO homeplayers SET allowedhomecount = ? WHERE uuid = ? ON DUPLICATE KEY UPDATE allowedhomecount = VALUES(allowedhomecount)";
                connection.prepareStatement(query).executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int getAllowedHomesCount(OfflinePlayer player) {
        return allowedHomes.get(player.getUniqueId());
    }

    public TextComponent getHomesListing(OfflinePlayer player, boolean admin) {
        LocaleManager localeManager = Landlord.getInstance().getLocaleManager();
        String prefix = localeManager.getMessage("prefix").trim();
        TextComponent textComponent = new TextComponent("");
        if (player == null || player.getName() == null) return textComponent;
        textComponent.addExtra(prefix);
        List<Home> homes = getHomes(player);
        if (homes.size() == 0) {
            textComponent.addExtra(admin ? prefix + localeManager.getMessage("player-no-homes").replaceAll("(?i)%PLAYER%", player.getName())
                    : prefix + localeManager.getMessage("no-homes"));
            return textComponent;
        }
        for (int i = 0; i < homes.size(); i++) {
            Home home = homes.get(i);

            String text = " " + localeManager.getMessage("homelisting-item").replaceAll("(?i)%HOME%", home.getName());
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(localeManager.getMessage("click-to-tp").replaceAll("(?i)%HOME%", home.getName())).create());
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, admin ? "/playerhome " + player.getName() + " " + home.getName() : "/cloudyhome " + home.getName());
            TextComponent homeComponent = new TextComponent("");
            homeComponent.setHoverEvent(hoverEvent);
            homeComponent.setClickEvent(clickEvent);
            homeComponent.addExtra(text);
            textComponent.addExtra(homeComponent);
            if (i != homes.size() - 1) {
                TextComponent comma = new TextComponent(ChatColor.GRAY + ",");
                comma.setHoverEvent(null);
                comma.setClickEvent(null);
                textComponent.addExtra(comma);
            }
        }
        return textComponent;
    }
}
