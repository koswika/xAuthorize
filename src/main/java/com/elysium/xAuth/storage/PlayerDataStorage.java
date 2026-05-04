package com.elysium.xAuth.storage;

import com.elysium.xAuth.XAuth;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataStorage {
    private final XAuth plugin;
    private final File file;
    private FileConfiguration config;

    public PlayerDataStorage(XAuth plugin) {
        this.plugin = plugin;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        file = new File(dataFolder, "playerdata.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml: " + e.getMessage());
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isRegistered(UUID uuid) {
        return config.contains(uuid.toString() + ".password");
    }

    public void registerPlayer(UUID uuid, String hashedPassword) {
        config.set(uuid.toString() + ".password", hashedPassword);
        save();
    }

    public void deletePlayer(UUID uuid) {
        config.set(uuid.toString(), null);
        save();
    }

    public String getStoredPassword(UUID uuid) {
        return config.getString(uuid.toString() + ".password");
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml: " + e.getMessage());
        }
    }
}
