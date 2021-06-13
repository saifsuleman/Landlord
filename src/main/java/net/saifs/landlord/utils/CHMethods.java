package net.saifs.landlord.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class CHMethods {
    public static String colour(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void send(CommandSender sender, String s) {
        sender.sendMessage(colour(s));
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            String s = player.getName();
            if (s == null) continue;
            if (s.equalsIgnoreCase(name)) return player;
        }
        return null;
    }

    public static boolean isAlphanumeric(String s) {
        return Pattern.compile("^[a-zA-Z0-9]*$").matcher(s).find();
    }
}
