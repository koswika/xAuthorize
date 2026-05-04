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
        String[] commandArgs = auth.consumePendingAuthCommand(player.getUniqueId(), "login");
        if (commandArgs != null) {
            args = commandArgs;
        }

        if (auth.isLoggedIn(player.getUniqueId())) {
            player.sendMessage(auth.getPrefix() + " §cYou are already logged in!");
            return true;
        }

        if (!auth.isRegistered(player.getUniqueId())) {
            player.sendMessage(auth.getPrefix() + " §cYou are not registered! Use §f/register <password> <confirm>§c.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(auth.getPrefix() + " §cUsage: §f/login <password>");
            return true;
        }

        if (!auth.login(player, args[0]) && !auth.isLockedOut(player.getUniqueId()) && auth.isRegistered(player.getUniqueId())) {
            player.sendMessage(auth.getPrefix() + " §cIncorrect password! Try again.");
        }

        return true;
    }
}
