package com.blakwurm.cloudyhomes.command;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.Home;
import com.blakwurm.cloudyhomes.handler.HomesManager;
import com.blakwurm.cloudyhomes.handler.PermissionHandler;
import com.blakwurm.cloudyhomes.utils.CHMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class SetHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_USER)) return true;
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, "&2&LHOMES &7»&a You must be a player to do this!");
            return true;
        }
        String name = args.length == 0 ? "home" : args[0];
        Player player = (Player) sender;
        HomesManager homesManager = CloudyHomes.getHomesManager();
        int allowed = homesManager.getAllowedHomesCount(player);
        List<Home> homes = homesManager.getHomes(player);

        if (homes.size() >= allowed) {
            CHMethods.send(player, "&2&LHOMES &7»&a You do not have enough homes to do that! Your home-limit is &7" + allowed + "&a!&6 You can purchase more homes at &nhttps://shop.cloudygaming.net/\"");
            return true;
        }

        if (!isAlphanumeric(name)) {
            CHMethods.send(player, "&2&LHOMES &7»&a Home names must be alphanumeric!");
            return true;
        }

        Home home = new Home(name, player, player.getLocation());
        homesManager.addHome(home);
        CHMethods.send(player, "&2&LHOMES &7»&a You have set a home named '&7" + name + "&a'");
        return true;
    }

    private boolean isAlphanumeric(String s) {
        return Pattern.compile("^[a-zA-Z0-9]*$").matcher(s).find();
    }
}
