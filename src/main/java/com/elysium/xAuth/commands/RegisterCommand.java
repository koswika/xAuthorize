package com.elysium.xAuth.commands;

import com.elysium.xAuth.manager.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    private final AuthManager auth;

    public RegisterCommand(AuthManager auth) {
        this.auth = auth;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (auth.isLoggedIn(player.getUniqueId())) {
            player.sendMessage("§8[§7XAuth§8] §cYou are already logged in!");
            return true;
        }

        if (auth.isRegistered(player.getName())) {
            player.sendMessage("§8[§7XAuth§8] §cYou are already registered! Use §f/login <password>§c.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§8[§7XAuth§8] §cUsage: §f/register <password> <confirmPassword>");
            return true;
        }

        if (!args[0].equals(args[1])) {
            player.sendMessage("§8[§7XAuth§8] §cPasswords do not match!");
            return true;
        }

        if (args[0].length() < 6) {
            player.sendMessage("§8[§7XAuth§8] §cPassword must be at least §f6 characters§c.");
            return true;
        }

        auth.register(player.getName(), args[0]);
        auth.forceLogin(player);
        player.sendTitle(
                "§7§lWelcome",
                "§7Registered as §f§l" + player.getName(),
                10, 80, 20
        );
        player.sendMessage("§8[§7XAuth§8] §aAccount created and logged in successfully!");
        return true;
    }
}