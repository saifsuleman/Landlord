package net.saifs.landlord;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public class Home {
    private String name;
    private OfflinePlayer owner;
    private Location location;

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
}
