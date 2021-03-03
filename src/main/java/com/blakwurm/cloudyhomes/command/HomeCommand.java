package com.blakwurm.cloudyhomes.command;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.Home;
import com.blakwurm.cloudyhomes.handler.HomesManager;
import com.blakwurm.cloudyhomes.handler.PermissionHandler;
import com.blakwurm.cloudyhomes.utils.CHMethods;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_USER)) return true;
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, "&2&LHOMES &7»&a You must be a player to do this!");
            return true;
        }
        Player player = (Player) sender;
        String name;
        HomesManager homesManager = CloudyHomes.getHomesManager();
        List<Home> homes = homesManager.getHomes(player);

        if (homes.size() == 0) {
            CHMethods.send(sender, "&2&LHOMES &7»&a You do not have any homes set!");
            return true;
        }

        if (args.length == 0) {
            if (homes.size() == 1) {
                name = homes.get(0).getName();
            } else {
                // TODO: replace with an interactive home listing
                BaseComponent[] homesListing = homesManager.getHomesListing(player, false);
                player.spigot().sendMessage(homesListing);
                return true;
            }
        } else {
            name = args[0];
        }

        for (Home home : homes) {
            if (home.getName().equals(name)) {
                player.teleport(home.getLocation());
                CHMethods.send(player, "&2&LHOMES &7»&a You have teleported to &7" + home.getName());
                return true;
            }
        }

        CHMethods.send(player, "&2&LHOMES &7»&a &7You do not have a home called &a" + name);
        return true;
    }
}
