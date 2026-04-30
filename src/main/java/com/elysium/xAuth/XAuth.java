package com.elysium.xAuth;

import com.elysium.xAuth.commands.LoginCommand;
import com.elysium.xAuth.commands.RegisterCommand;
import com.elysium.xAuth.commands.ResetPasswordCommand;
import com.elysium.xAuth.listeners.PlayerListener;
import com.elysium.xAuth.manager.AuthManager;
import com.elysium.xAuth.storage.PlayerDataStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Filter;
import java.util.logging.Logger;

public class XAuth extends JavaPlugin {

    private AuthManager authManager;
    private PlayerDataStorage playerDataStorage;

    @Override
    public void onEnable() {
        silenceLogs();
        saveDefaultConfig();

        playerDataStorage = new PlayerDataStorage(this);
        authManager = new AuthManager(this, playerDataStorage);

        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCommand(authManager));
        Objects.requireNonNull(getCommand("register")).setExecutor(new RegisterCommand(authManager));
        Objects.requireNonNull(getCommand("xreset")).setExecutor(new ResetPasswordCommand(authManager, playerDataStorage));

        getServer().getPluginManager().registerEvents(new PlayerListener(authManager, this), this);
    }

    @Override
    public void onDisable() {
        //Plugin shutdown login
    }

    private void silenceLogs() {
        getLogger().setFilter(record -> false);

        Logger rootLogger = Logger.getLogger("");
        Filter existing = rootLogger.getFilter();
        rootLogger.setFilter(record -> {
            String msg = record.getMessage();
            if (msg == null) return existing == null || existing.isLoggable(record);

            if (msg.contains("playerdata.yml") ||
                    msg.contains("[XAuth]") ||
                    msg.contains("com.elysium.xAuth")) {
                return false;
            }

            return existing == null || existing.isLoggable(record);
        });
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }
}