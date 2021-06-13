package net.saifs.landlord.command;

import net.saifs.landlord.Landlord;
import net.saifs.landlord.Home;
import net.saifs.landlord.handler.PermissionHandler;
import net.saifs.landlord.utils.CHMethods;
import net.saifs.landlord.utils.LocaleManager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerHomeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_ADMIN)) return true;
        LocaleManager localeManager = Landlord.getInstance().getLocaleManager();
        String prefix = localeManager.getMessage("prefix");
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, localeManager.getMessage("must-be-player"));
            return true;
        }
        if (args.length == 0) {
            CHMethods.send(sender, prefix + localeManager.getMessage("playerhome-usage"));
            return true;
        }
        Player playerSender = (Player) sender;
        OfflinePlayer player = CHMethods.getOfflinePlayer(args[0]);
        if (player == null) {
            CHMethods.send(sender, prefix + localeManager.getMessage("cannot-find-player").replaceAll("(?i)%PLAYER%", args[0]));
            return true;
        }
        if (args.length == 1) {
            BaseComponent[] homesListing = Landlord.getHomesManager().getHomesListing(player, true);
            playerSender.spigot().sendMessage(homesListing);
            return true;
        }
        String name = args[1];
        List<Home> homes = Landlord.getHomesManager().getHomes(player);
        for (Home home : homes) {
            if (home.getName().equals(name)) {
                playerSender.teleport(home.getLocation());
                if (player.getName() != null)
                    CHMethods.send(sender, prefix + localeManager.getMessage("you-have-teleported"
                            .replaceAll("(?i)%PLAYER%", player.getName()).replaceAll("(?i)%HOME%", home.getName())));
                return true;
            }
        }
        CHMethods.send(sender, prefix + localeManager.getMessage("playerhome-no-home-called").replaceAll("(?i)%HOME%", name));
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        }
        if (args.length == 2) {
            OfflinePlayer offlinePlayer = CHMethods.getOfflinePlayer(args[0]);
            if (offlinePlayer != null) {
                return Landlord.getHomesManager().getHomeNames(offlinePlayer);
            }
        }
        return new ArrayList<>();
    }
}
