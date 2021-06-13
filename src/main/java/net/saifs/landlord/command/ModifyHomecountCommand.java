package net.saifs.landlord.command;

import net.saifs.landlord.Landlord;
import net.saifs.landlord.handler.HomesManager;
import net.saifs.landlord.handler.PermissionHandler;
import net.saifs.landlord.utils.CHMethods;
import net.saifs.landlord.utils.LocaleManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ModifyHomecountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_ADMIN)) return true;
        LocaleManager localeManager = Landlord.getInstance().getLocaleManager();
        String prefix = localeManager.getMessage("prefix");
        if (args.length < 2) {
            CHMethods.send(sender, prefix + localeManager.getMessage("modifyhomecount-usage"));
            return true;
        }
        OfflinePlayer target = CHMethods.getOfflinePlayer(args[0]);
        if (target == null) {
            CHMethods.send(sender, prefix + localeManager.getMessage("cannot-find-player").replaceAll("%PLAYER%", args[0]));
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            CHMethods.send(sender, prefix + localeManager.getMessage("modifyhomecount-usage"));
            return true;
        }
        String name = target.getName();
        if (name == null) name = args[0];
        HomesManager homesManager = Landlord.getHomesManager();
        homesManager.setAllowedHomesCount(target.getUniqueId(), homesManager.getAllowedHomesCount(target) + amount);
        CHMethods.send(sender, prefix + localeManager.getMessage("updated-homecount")
                .replaceAll("%PLAYER%", name)
                .replaceAll("%HOMECOUNT%",
                        String.valueOf(homesManager.getAllowedHomesCount(target))));
        return true;
    }
}
