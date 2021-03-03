package com.blakwurm.cloudyhomes.command;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.handler.PermissionHandler;
import com.blakwurm.cloudyhomes.utils.CHMethods;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_USER)) return true;
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, "&2&LHOMES &7»&a You must be a player to do this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length > 0) {
            if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_ADMIN)) return true;
            OfflinePlayer offlinePlayer = CHMethods.getOfflinePlayer(args[0]);
            if (offlinePlayer == null) {
                CHMethods.send(sender, "&2&LHOMES &7»&a Unable to find that player!");
                return true;
            }
            player.spigot().sendMessage(CloudyHomes.getHomesManager().getHomesListing(offlinePlayer, true));
            return true;
        }
        player.spigot().sendMessage(CloudyHomes.getHomesManager().getHomesListing(player, false));
        return true;
    }
}
