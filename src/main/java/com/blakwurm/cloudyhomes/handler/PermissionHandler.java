package com.blakwurm.cloudyhomes.handler;

import com.blakwurm.cloudyhomes.utils.CHMethods;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionHandler {
    public static final String HOME_USER = "cloudyhomes.user";
    public static final String HOME_ADMIN = "cloudyhomes.admin";

    public static boolean hasPermission(CommandSender sender, String perm) {
        boolean permitted = sender.hasPermission(perm);
        if (!permitted) {
            CHMethods.send(sender, "&cError: &4You do not have permission to do that!");
        }
        return permitted;
    }
}
