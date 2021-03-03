package com.blakwurm.cloudyhomes.command;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.handler.HomesManager;
import com.blakwurm.cloudyhomes.handler.PermissionHandler;
import com.blakwurm.cloudyhomes.utils.CHMethods;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ModifyHomecountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_ADMIN)) return true;
        if (args.length < 2) {
            CHMethods.send(sender, "&2&LHOMES &7»&a Usage: /modifyhomecount <player> <amount>");
            return true;
        }
        OfflinePlayer target = CHMethods.getOfflinePlayer(args[0]);
        if (target == null) {
            CHMethods.send(sender, "&2&LHOMES &7»&a Unable to find the player '&7" + args[0] + "&a'");
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            CHMethods.send(sender, "&2&LHOMES &7»&a Usage: /modifyhomecount <player> <amount>");
            return true;
        }
        HomesManager homesManager = CloudyHomes.getHomesManager();
        homesManager.setAllowedHomesCount(target.getUniqueId(), homesManager.getAllowedHomesCount(target) + amount);
        CHMethods.send(sender, "&2&LHOMES &7»&7 " + target.getName() + "&a now has &7" + homesManager.getAllowedHomesCount(target) + "&a allowed homes.");
        return true;
    }
}
