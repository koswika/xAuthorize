package com.elysium.xAuth.commands;

import com.elysium.xAuth.manager.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    private final AuthManager auth;

    public LoginCommand(AuthManager auth) {
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

        if (!auth.isRegistered(player.getName())) {
            player.sendMessage("§8[§7XAuth§8] §cYou are not registered! Use §f/register <password> <confirm>§c.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§8[§7XAuth§8] §cUsage: §f/login <password>");
            return true;
        }

        if (!auth.login(player, args[0])) {
            player.sendMessage("§8[§7XAuth§8] §cIncorrect password! Try again.");
        }

        return true;
    }
}