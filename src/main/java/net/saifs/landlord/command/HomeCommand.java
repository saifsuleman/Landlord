package net.saifs.landlord.command;

import net.saifs.landlord.Landlord;
import net.saifs.landlord.Home;
import net.saifs.landlord.handler.HomesManager;
import net.saifs.landlord.handler.PermissionHandler;
import net.saifs.landlord.utils.CHMethods;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionHandler.hasPermission(sender, PermissionHandler.HOME_USER)) return true;
        if (!(sender instanceof Player)) {
            CHMethods.send(sender, "&2&LHOMES &7»&a You must be a player to do this!");
            return true;
        }
        Player player = (Player) sender;
        String name;
        HomesManager homesManager = Landlord.getHomesManager();
        List<Home> homes = homesManager.getHomes(player);

        if (homes.size() == 0) {
            CHMethods.send(sender, "&2&LHOMES &7»&a You do not have any homes set!");
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
                CHMethods.send(sender, "home name must be alphasnumeric");
                return true;
            }
            name = args[0];
        }

        for (Home home : homes) {
            if (home.getName().equals(name)) {
                player.teleport(home.getLocation());
                CHMethods.send(player, "&2&LHOMES &7»&a You have teleported to &7" + home.getName());
                return true;
            }
        }

        CHMethods.send(player, "&2&LHOMES &7»&a &7You do not have a home called &a" + name);
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
