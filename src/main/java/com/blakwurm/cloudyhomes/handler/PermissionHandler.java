package com.blakwurm.cloudyhomes.handler;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.utils.CHMethods;
import com.blakwurm.cloudyhomes.utils.LocaleManager;
import org.bukkit.command.CommandSender;

public class PermissionHandler {
    public static final String HOME_USER = "cloudyhomes.user";
    public static final String HOME_ADMIN = "cloudyhomes.admin";

    public static boolean hasPermission(CommandSender sender, String perm) {
        LocaleManager localeManager = CloudyHomes.getInstance().getLocaleManager();

        boolean permitted = sender.hasPermission(perm);
        if (!permitted) {
            CHMethods.send(sender, localeManager.getMessage("no-permission"));
        }
        return permitted;
    }
}
