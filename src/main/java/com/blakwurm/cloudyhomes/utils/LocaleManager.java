package com.blakwurm.cloudyhomes.utils;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.config.Config;
import org.bukkit.ChatColor;

public class LocaleManager {
    private Config config;

    public LocaleManager() {
        this.config = new Config("locale.yml");
    }

    public String getMessage(String s) {
        if (!config.getConfig().contains(s)) {
            CloudyHomes.getInstance().getLogger().severe("ILLEGAL LOCALE FETCH: " + s);
            return ChatColor.RED + "There was a locale error. Message: " + s;
        }

        return CHMethods.colour(config.getConfig().getString(s));
    }

    public enum Messages {
        NO_PERMISSION("no-permission"), PREFIX("prefix"), PLAYER_NO_HOMES("player-no-homes"),
        NO_HOMES("no-homes"), HOMELISTING("homelisting-item"), CLICK_TO_TP("click-to-tp"),
        NOT_ENOUGH_HOMES("not-enough-homes"), MUST_BE_ALPHANUMERIC("must-be-alphanumeric");
        private String value;

        Messages(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
