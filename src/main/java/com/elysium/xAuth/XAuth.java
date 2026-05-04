package com.elysium.xAuth;

import com.elysium.xAuth.commands.LoginCommand;
import com.elysium.xAuth.commands.RegisterCommand;
import com.elysium.xAuth.commands.ResetPasswordCommand;
import com.elysium.xAuth.commands.AuthTabCompleter;
import com.elysium.xAuth.listeners.PlayerListener;
import com.elysium.xAuth.manager.AuthManager;
import com.elysium.xAuth.storage.PlayerDataStorage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class XAuth extends JavaPlugin {

    private AuthManager authManager;
    private PlayerDataStorage playerDataStorage;
    private String prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        prefix = getConfig().getString("prefix", "§8[§7XAuth§8]");

        playerDataStorage = new PlayerDataStorage(this);
        authManager = new AuthManager(this, playerDataStorage);

        PluginCommand loginCmd = getCommand("login");
        if (loginCmd == null) {
            getLogger().severe("Command 'login' not found in plugin.yml! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        loginCmd.setExecutor(new LoginCommand(authManager));
        loginCmd.setTabCompleter(new AuthTabCompleter());

        PluginCommand registerCmd = getCommand("register");
        if (registerCmd == null) {
            getLogger().severe("Command 'register' not found in plugin.yml! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerCmd.setExecutor(new RegisterCommand(authManager, getConfig()));
        registerCmd.setTabCompleter(new AuthTabCompleter());

        PluginCommand xresetCmd = getCommand("xreset");
        if (xresetCmd == null) {
            getLogger().severe("Command 'xreset' not found in plugin.yml! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        xresetCmd.setExecutor(new ResetPasswordCommand(authManager, playerDataStorage));

        getServer().getPluginManager().registerEvents(new PlayerListener(authManager, this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }

    public String getPrefix() {
        return prefix;
    }
}
