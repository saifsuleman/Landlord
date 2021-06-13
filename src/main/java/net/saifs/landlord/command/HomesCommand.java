package net.saifs.landlord.command;

import net.saifs.landlord.Landlord;
import net.saifs.landlord.handler.PermissionHandler;
import net.saifs.landlord.utils.CHMethods;
import net.saifs.landlord.utils.LocaleManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_USER)) return true;
        LocaleManager localeManager = Landlord.getInstance().getLocaleManager();
        String prefix = localeManager.getMessage("prefix");
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, prefix + localeManager.getMessage("must-be-player"));
            return true;
        }
        Player player = (Player) sender;
        if (args.length > 0) {
            if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_ADMIN)) return true;
            OfflinePlayer offlinePlayer = CHMethods.getOfflinePlayer(args[0]);
            if (offlinePlayer == null) {
                CHMethods.send(sender, prefix + localeManager.getMessage("cannot-find-player"));
                return true;
            }
            player.spigot().sendMessage(Landlord.getHomesManager().getHomesListing(offlinePlayer, true));
            return true;
        }
        player.spigot().sendMessage(Landlord.getHomesManager().getHomesListing(player, false));
        return true;
    }
}
