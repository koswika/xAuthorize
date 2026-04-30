package com.elysium.xAuth.storage;

import com.elysium.xAuth.XAuth;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class PlayerDataStorage {
    private final File file;
    private FileConfiguration config;

    public PlayerDataStorage(XAuth plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        file = new File(dataFolder, "playerdata.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isRegistered(String username) {
        return config.contains(username.toLowerCase() + ".password");
    }

    public void registerPlayer(String username, String hashedPassword) {
        config.set(username.toLowerCase() + ".password", hashedPassword);
        save();
    }

    public void deletePlayer(String username) {
        config.set(username.toLowerCase(), null);
        save();
    }

    public String getStoredPassword(String username) {
        return config.getString(username.toLowerCase() + ".password");
    }

    private void save() {
        try {
            config.save(file);
            config = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}