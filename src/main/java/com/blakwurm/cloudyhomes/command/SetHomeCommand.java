package com.blakwurm.cloudyhomes.command;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.Home;
import com.blakwurm.cloudyhomes.handler.HomesManager;
import com.blakwurm.cloudyhomes.handler.PermissionHandler;
import com.blakwurm.cloudyhomes.utils.CHMethods;
import com.blakwurm.cloudyhomes.utils.LocaleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_USER)) return true;
        LocaleManager localeManager = CloudyHomes.getInstance().getLocaleManager();
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, localeManager.getMessage("must-be-player"));
            return true;
        }

        String prefix = localeManager.getMessage("prefix");

        String name = args.length == 0 ? "home" : args[0];
        Player player = (Player) sender;
        HomesManager homesManager = CloudyHomes.getHomesManager();
        int allowed = homesManager.getAllowedHomesCount(player);
        List<Home> homes = homesManager.getHomes(player);

        if (homes.size() >= allowed) {
            CHMethods.send(player, prefix + localeManager.getMessage("not-enough-homes").replaceAll("(?i)%LIMIT%", String.valueOf(allowed)));
            return true;
        }

        if (!CHMethods.isAlphanumeric(name)) {
            CHMethods.send(player, prefix + localeManager.getMessage("must-be-alphanumeric"));
            return true;
        }

        Home home = new Home(name, player, player.getLocation());
        homesManager.addHome(home);
        CHMethods.send(player, prefix + localeManager.getMessage("sethome").replaceAll("(?i)%HOME%", name));
        return true;
    }
}
