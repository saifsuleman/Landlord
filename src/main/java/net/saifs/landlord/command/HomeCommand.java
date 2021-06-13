package net.saifs.landlord.command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.saifs.landlord.Home;
import net.saifs.landlord.Landlord;
import net.saifs.landlord.handler.HomesManager;
import net.saifs.landlord.handler.PermissionHandler;
import net.saifs.landlord.utils.CHMethods;
import net.saifs.landlord.utils.LocaleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {
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
        String name;
        HomesManager homesManager = Landlord.getHomesManager();
        List<Home> homes = homesManager.getHomes(player);

        if (homes.size() == 0) {
            CHMethods.send(sender, prefix + localeManager.getMessage("no-homes"));
            return true;
        }

        if (args.length == 0) {
            if (homes.size() == 1) {
                name = homes.get(0).getName();
            } else {
                BaseComponent[] homesListing = homesManager.getHomesListing(player, false);
                player.spigot().sendMessage(homesListing);
                return true;
            }
        } else {
            if (!CHMethods.isAlphanumeric(args[0])) {
                // TODO: Locale file
                CHMethods.send(sender, prefix + localeManager.getMessage("must-be-alphanumeric"));
                return true;
            }
            name = args[0];
        }

        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                player.teleport(home.getLocation());
                CHMethods.send(player, prefix + localeManager.getMessage("you-have-teleported")
                        .replaceAll("%HOME%", home.getName()));
                return true;
            }
        }

        CHMethods.send(player, prefix + localeManager.getMessage("no-home-exists").replaceAll("%HOME%", name));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            return Landlord.getHomesManager().getHomeNames(player);
        }
        return new ArrayList<>();
    }
}
