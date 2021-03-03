package com.blakwurm.cloudyhomes.command;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.Home;
import com.blakwurm.cloudyhomes.handler.PermissionHandler;
import com.blakwurm.cloudyhomes.utils.CHMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteHomeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_USER)) return true;
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, "&2&LHOMES &7»&a You must be a player to do this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            CHMethods.send(sender, "&2&LHOMES &7»&a Usage: /deletehome <home>");
            return true;
        }
        List<Home> homes = CloudyHomes.getHomesManager().getHomes(player);
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(args[0])) {
                CloudyHomes.getHomesManager().removeHome(home);
                CHMethods.send(sender, "&2&LHOMES &7»&a Deleted home: &7" + home.getName());
                return true;
            }
        }
        CHMethods.send(sender, "&2&LHOMES &7»&a You do not have a home named '&7" + args[0] + "&a'");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            return CloudyHomes.getHomesManager().getHomeNames(player);
        }
        return new ArrayList<>();
    }
}
