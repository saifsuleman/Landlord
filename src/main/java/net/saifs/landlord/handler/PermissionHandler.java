package net.saifs.landlord.handler;

import net.saifs.landlord.Landlord;
import net.saifs.landlord.utils.CHMethods;
import net.saifs.landlord.utils.LocaleManager;
import org.bukkit.command.CommandSender;

public class PermissionHandler {
    public static final String HOME_USER = "landlord.user";
    public static final String HOME_ADMIN = "landlord.admin";

    public static boolean hasPermission(CommandSender sender, String perm) {
        LocaleManager localeManager = Landlord.getInstance().getLocaleManager();

        boolean permitted = sender.hasPermission(perm);
        if (!permitted) {
            CHMethods.send(sender, localeManager.getMessage("no-permission"));
        }
        return permitted;
    }
}
