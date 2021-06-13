package net.saifs.landlord.handler;

import net.md_5.bungee.api.chat.*;
import net.saifs.landlord.Home;
import net.saifs.landlord.Landlord;
import net.saifs.landlord.utils.LocaleManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HomesManager {
    private Connection sql;

    public HomesManager(Connection sql) {
        this.sql = sql;
    }

    public List<Home> getHomes(OfflinePlayer player) {
        return getHomes().stream().filter(home -> home.getOwner().equals(player)).collect(Collectors.toList());
    }

    public List<Home> getHomes() {
        List<Home> homes = new ArrayList<>();
        try {
            this.sql.prepareStatement(Landlord.getInstance().getPluginConfig().getConfig().getBoolean("mysql.enabled") ?
                    "CREATE TABLE IF NOT EXISTS homes (id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY, homename VARCHAR(255) NOT NULL, owner VARCHAR(255) NOT NULL, x FLOAT(25) NOT NULL, y "
                            + "FLOAT(25) NOT NULL, z FLOAT(25) NOT NULL, yaw FLOAT(10) NOT NULL, pitch FLOAT(10) NOT NULL, world VARCHAR(255) NOT NULL)" :
                    "CREATE TABLE IF NOT EXISTS homes (id INTEGER PRIMARY KEY AUTOINCREMENT, homename TEXT(255) NOT NULL, owner TEXT(255) NOT NULL, " +
                            "x REAL(25) NOT NULL, y REAL(25) NOT NULL, z REAL(25) NOT NULL, yaw REAL(10) NOT NULL, pitch REAL(10) NOT NULL, world TEXT(255) NOT NULL)").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String query = "SELECT * FROM homes";
            ResultSet results = this.sql.prepareStatement(query).executeQuery();

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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return homes;
    }

    public void addHome(Home home) {
        // Deletes home if exists - this ensures one set-home will override another.
        removeHome(home);

        Location loc = home.getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        World world = home.getLocation().getWorld();
        if (world == null) return;
        try {
            String query = "INSERT INTO homes (homename, owner, x, y, z, yaw, pitch, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = this.sql.prepareStatement(query);
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
    }

    public void removeHome(Home home) {
        try {
            String query = "DELETE FROM homes WHERE homename=? AND owner=?";
            PreparedStatement statement = this.sql.prepareStatement(query);
            statement.setString(1, home.getName());
            statement.setString(2, home.getOwner().getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getHomeNames(OfflinePlayer player) {
        return getHomes(player).stream().map(Home::getName).collect(Collectors.toList());
    }

    public Map<UUID, Integer> getAllowedHomesCount() {
        Map<UUID, Integer> map = new HashMap<>();
        try {
            String query = "CREATE TABLE IF NOT EXISTS homeplayers (uuid VARCHAR(255) PRIMARY KEY, allowedhomecount INT(10) NOT NULL)";
            this.sql.prepareStatement(query).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String query = "SELECT * FROM homeplayers";
            ResultSet results = this.sql.prepareStatement(query).executeQuery();

            while (results.next()) {
                UUID uuid = UUID.fromString(results.getString("uuid"));
                int allowed = results.getInt("allowedhomecount");
                map.put(uuid, allowed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void setAllowedHomesCount(UUID uuid, int count) {
        try {
            String hasQuery = "SELECT * FROM homeplayers WHERE uuid=?";
            PreparedStatement hasStatement = this.sql.prepareStatement(hasQuery);
            hasStatement.setString(1, uuid.toString());
            ResultSet hasResults = hasStatement.executeQuery();
            boolean has = hasResults.next();

            String query = has ? "UPDATE homeplayers SET allowedhomecount=? WHERE uuid=?" : "INSERT INTO homeplayers (allowedhomecount, uuid) VALUES (?, ?)";
            PreparedStatement statement = sql.prepareStatement(query);
            statement.setInt(1, count);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAllowedHomesCount(OfflinePlayer player) {
        Map<UUID, Integer> map = getAllowedHomesCount();
        if (map.containsKey(player.getUniqueId())) return map.get(player.getUniqueId());
        int defaultHomeCount = Landlord.getInstance().getPluginConfig().getConfig().getInt("defaultHomeCount");
        setAllowedHomesCount(player.getUniqueId(), defaultHomeCount);
        return defaultHomeCount;
    }
//
//    public BaseComponent[] fromLegacyText(String message) {
//        net.md_5.bungee.api.ChatColor defaultColor = net.md_5.bungee.api.ChatColor.WHITE;
//        Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
//        ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();
//        StringBuilder builder = new StringBuilder();
//        TextComponent component = new TextComponent();
//        Matcher matcher = url.matcher(message);
//
//        for (int i = 0; i < message.length(); i++) {
//            char c = message.charAt(i);
//            if (c == net.md_5.bungee.api.ChatColor.COLOR_CHAR) {
//                if (++i >= message.length()) {
//                    break;
//                }
//                c = message.charAt(i);
//                if (c >= 'A' && c <= 'Z') {
//                    c += 32;
//                }
//                net.md_5.bungee.api.ChatColor format;
//                if (c == 'x' && i + 12 < message.length()) {
//                    StringBuilder hex = new StringBuilder("#");
//                    for (int j = 0; j < 6; j++) {
//                        hex.append(message.charAt(i + 2 + (j * 2)));
//                    }
//                    try {
//                        format = net.md_5.bungee.api.ChatColor.of(hex.toString());
//                    } catch (IllegalArgumentException ex) {
//                        format = null;
//                    }
//
//                    i += 12;
//                } else {
//                    format = net.md_5.bungee.api.ChatColor.getByChar(c);
//                }
//                if (format == null) {
//                    continue;
//                }
//                if (builder.length() > 0) {
//                    TextComponent old = component;
//                    component = new TextComponent(old);
//                    old.setText(builder.toString());
//                    builder = new StringBuilder();
//                    components.add(old);
//                }
//                if (format == net.md_5.bungee.api.ChatColor.BOLD) {
//                    component.setBold(true);
//                } else if (format == net.md_5.bungee.api.ChatColor.ITALIC) {
//                    component.setItalic(true);
//                } else if (format == net.md_5.bungee.api.ChatColor.UNDERLINE) {
//                    component.setUnderlined(true);
//                } else if (format == net.md_5.bungee.api.ChatColor.STRIKETHROUGH) {
//                    component.setStrikethrough(true);
//                } else if (format == net.md_5.bungee.api.ChatColor.MAGIC) {
//                    component.setObfuscated(true);
//                } else if (format == net.md_5.bungee.api.ChatColor.RESET) {
//                    format = defaultColor;
//                    component = new TextComponent();
//                    component.setColor(format);
//
//                    component.setBold(false);
//                    component.setItalic(false);
//                    component.setStrikethrough(false);
//                    component.setUnderlined(false);
//                    component.setObfuscated(false);
//                } else {
//                    component = new TextComponent();
//                    component.setColor(format);
//
//                    component.setBold(false);
//                    component.setItalic(false);
//                    component.setStrikethrough(false);
//                    component.setUnderlined(false);
//                    component.setObfuscated(false);
//                }
//                continue;
//            }
//            int pos = message.indexOf(' ', i);
//            if (pos == -1) {
//                pos = message.length();
//            }
//            if (matcher.region(i, pos).find()) { //Web link handling
//
//                if (builder.length() > 0) {
//                    TextComponent old = component;
//                    component = new TextComponent(old);
//                    old.setText(builder.toString());
//                    builder = new StringBuilder();
//                    components.add(old);
//                }
//
//                TextComponent old = component;
//                component = new TextComponent(old);
//                String urlString = message.substring(i, pos);
//                component.setText(urlString);
//                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
//                        urlString.startsWith("http") ? urlString : "http://" + urlString));
//                components.add(component);
//                i += pos - i - 1;
//                component = old;
//                continue;
//            }
//            builder.append(c);
//        }
//
//        component.setText(builder.toString());
//        components.add(component);
//
//        return components.toArray(new BaseComponent[components.size()]);
//    }

    private String fromLegacyText(String s) {
        return s;
    }

    public BaseComponent[] getHomesListing(OfflinePlayer player, boolean admin) {
        LocaleManager localeManager = Landlord.getInstance().getLocaleManager();
        String prefix = localeManager.getMessage("prefix");
        ComponentBuilder builder = new ComponentBuilder("");
        if (player == null || player.getName() == null) return builder.create();
        List<Home> homes = getHomes(player);
        if (homes.size() == 0) {
            builder.append(fromLegacyText(admin ? prefix + localeManager.getMessage("player-no-homes").replaceAll("(?i)%PLAYER%", player.getName())
                    : prefix + localeManager.getMessage("no-homes")));
            return builder.create();
        }
        builder.append(fromLegacyText(prefix));
        for (int i = 0; i < homes.size(); i++) {
            Home home = homes.get(i);
            String text = localeManager.getMessage("homelisting-item").replaceAll("(?i)%HOME%", home.getName());
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(localeManager.getMessage("click-to-tp").replaceAll("(?i)%HOME%", home.getName())).create());
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, admin ? "/playerhome " + player.getName() + " " + home.getName() : "/cloudyhome " + home.getName());
            builder.append(fromLegacyText(String.valueOf(ChatColor.RESET))).event(hoverEvent).event(clickEvent).append(fromLegacyText(text));
            if (i != homes.size() - 1) {
                builder.append(fromLegacyText(ChatColor.GRAY + ", "));
            }
        }
        return builder.create();
    }
}
