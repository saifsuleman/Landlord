package net.saifs.landlord;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public class Home {
    private final String name;
    private final OfflinePlayer owner;
    private final Location location;

    public Home(String name, OfflinePlayer owner, Location location) {
        this.name = name;
        this.owner = owner;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isOwner(OfflinePlayer player) {
        return owner.getUniqueId().equals(player.getUniqueId());
    }
}
