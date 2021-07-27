package net.saifs.landlord.command;

import net.saifs.landlord.Home;
import net.saifs.landlord.Landlord;
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

public class DeleteHomeCommand implements CommandExecutor, TabCompleter {
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
        if (args.length == 0) {
            CHMethods.send(sender, "&2&LHOMES &7»&a Usage: /deletehome <home>");
            return true;
        }
        List<Home> homes = Landlord.getHomesManager().getHomes(player);
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(args[0])) {
                Landlord.getHomesManager().removeHome(home);
                CHMethods.send(sender, "&2&LHOMES &7»&a Deleted home: &7" + home.getName());
                return true;
            }
        }
        CHMethods.send(sender, prefix + localeManager.getMessage("no-home-exists").replaceAll("%HOME%", args[0]));

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
