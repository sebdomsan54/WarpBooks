package ua.lokha.warpbooks;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config extends YamlConfiguration {
    private File file;

    public Config(File file) {
        this.file = file;
        this.reload();
    }

    public void reload() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    public <T> T getOrSet(String path, T def) {
        if (this.contains(path)) {
            return (T) this.get(path);
        } else {
            this.set(path, def);
            this.save();
            return def;
        }
    }

    public void save() {
        try {
            this.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
