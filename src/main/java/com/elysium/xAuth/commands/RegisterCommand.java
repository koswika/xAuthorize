package com.elysium.xAuth.commands;

import com.elysium.xAuth.manager.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    private final AuthManager auth;
    private final FileConfiguration config;

    public RegisterCommand(AuthManager auth, FileConfiguration config) {
        this.auth = auth;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        String[] commandArgs = auth.consumePendingAuthCommand(player.getUniqueId(), "register");
        if (commandArgs != null) {
            args = commandArgs;
        }

        if (auth.isLoggedIn(player.getUniqueId())) {
            player.sendMessage(auth.getPrefix() + " §cYou are already logged in!");
            return true;
        }

        if (auth.isRegistered(player.getUniqueId())) {
            player.sendMessage(auth.getPrefix() + " §cYou are already registered! Use §f/login <password>§c.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(auth.getPrefix() + " §cUsage: §f/register <password> <confirmPassword>");
            return true;
        }

        if (!args[0].equals(args[1])) {
            player.sendMessage(auth.getPrefix() + " §cPasswords do not match!");
            return true;
        }

        int minPasswordLength = config.getInt("min-password-length", 6);
        if (args[0].length() < minPasswordLength) {
            player.sendMessage(auth.getPrefix() + " §cPassword must be at least §f" + minPasswordLength + " characters§c.");
            return true;
        }

        auth.register(player.getUniqueId(), args[0]);
        auth.forceLogin(player);
        player.sendTitle(
                "§7§lWelcome",
                "§7Registered as §f§l" + player.getName(),
                10, 80, 20
        );
        player.sendMessage(auth.getPrefix() + " §aAccount created and logged in successfully!");
        return true;
    }
}
