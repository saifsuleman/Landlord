package com.blakwurm.cloudyhomes.command;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.Home;
import com.blakwurm.cloudyhomes.handler.PermissionHandler;
import com.blakwurm.cloudyhomes.utils.CHMethods;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerHomeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_ADMIN)) return true;
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, "&2&LHOMES &7»&a You must be a player to do this!");
            return true;
        }
        if (args.length == 0) {
            CHMethods.send(sender, "&2&LHOMES &7»&a Usage: /playerhome <player> <home>");
            return true;
        }
        Player playerSender = (Player) sender;
        OfflinePlayer player = CHMethods.getOfflinePlayer(args[0]);
        if (player == null) {
            CHMethods.send(sender, "&2&LHOMES &7»&a Unable to find the player '&7" + args[0] + "&a'");
            return true;
        }
        if (args.length == 1) {
            BaseComponent[] homesListing = CloudyHomes.getHomesManager().getHomesListing(player, true);
            playerSender.spigot().sendMessage(homesListing);
            return true;
        }
        String name = args[1];
        List<Home> homes = CloudyHomes.getHomesManager().getHomes(player);
        for (Home home : homes) {
            if (home.getName().equals(name)) {
                playerSender.teleport(home.getLocation());
                CHMethods.send(sender, "&2&LHOMES &7»&a You have teleported to &7" + player.getName() + "&a's home &7" + home.getName());
                return true;
            }
        }
        CHMethods.send(sender, "&2&LHOMES &7»&a &7That player does not have a home called &a" + name);
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        }
        if (args.length == 2) {
            OfflinePlayer offlinePlayer = CHMethods.getOfflinePlayer(args[0]);
            if (offlinePlayer != null) {
                return CloudyHomes.getHomesManager().getHomeNames(offlinePlayer);
            }
        }
        return new ArrayList<>();
    }
}
