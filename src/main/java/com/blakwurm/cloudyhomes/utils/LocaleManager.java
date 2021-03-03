package com.blakwurm.cloudyhomes.utils;

import com.blakwurm.cloudyhomes.CloudyHomes;
import com.blakwurm.cloudyhomes.config.Config;
import org.bukkit.ChatColor;

public class LocaleManager {
    private Config config;

    public LocaleManager() {
        this.config = new Config("locale.yml");
    }

    public String getMessage(Messages messages) {
        if (!config.getConfig().contains(messages.value())) {
            CloudyHomes.getInstance().getLogger().severe("ILLEGAL LOCALE FETCH: " + messages.value());
            return ChatColor.RED + "There was a locale error. Message: " + messages.value();
        }

        return CHMethods.colour(config.getConfig().getString(messages.value()));
    }

    public enum Messages {
        NO_PERMISSION("no-permission"), PREFIX("prefix"), PLAYER_NO_HOMES("player-no-homes"),
        NO_HOMES("no-homes"), HOMELISTING("homelisting-item"), CLICK_TO_TP("click-to-tp");
        private String value;

        Messages(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
