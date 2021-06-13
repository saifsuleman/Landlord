package net.saifs.landlord.config;

import net.saifs.landlord.Landlord;
import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {
    final Landlord PLUGIN = Landlord.getInstance();

    private String name;
    private File file;
    private FileConfiguration config;

    public Config(String name, String defaultFile) {
        if (!name.toLowerCase().endsWith(".yml")) {
            name += ".yml";
        }
        this.name = name;
        load(PLUGIN.getResource(defaultFile));
    }

    public Config(String name) {
        this(name, name);
    }

    private void load(InputStream stream) {
        try {
            file = new File(PLUGIN.getDataFolder() + "/" + name);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            config = YamlConfiguration.loadConfiguration(file);
            if (stream != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream, Charsets.UTF_8)));
                config.options().copyDefaults(true);
            }
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
