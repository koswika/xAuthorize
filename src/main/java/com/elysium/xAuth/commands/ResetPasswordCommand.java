package com.elysium.xAuth.commands;

import com.elysium.xAuth.manager.AuthManager;
import com.elysium.xAuth.storage.PlayerDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetPasswordCommand implements CommandExecutor {

    private final AuthManager auth;
    private final PlayerDataStorage storage;

    public ResetPasswordCommand(AuthManager auth, PlayerDataStorage storage) {
        this.auth = auth;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("xauth.reset")) {
            sender.sendMessage("§8[§7XAuth§8] §cYou do not have permission to do this.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§8[§7XAuth§8] §cUsage: §f/xreset <player>");
            return true;
        }

        String targetName = args[0];

        if (!auth.isRegistered(targetName)) {
            sender.sendMessage("§8[§7XAuth§8] §cPlayer §f" + targetName + " §cis not registered.");
            return true;
        }

        storage.deletePlayer(targetName);

        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null && online.isOnline()) {
            auth.logout(online.getUniqueId());
            online.kickPlayer("§8[§7XAuth§8] §eYour account has been reset.\n§fPlease rejoin and register again.");
        }

        sender.sendMessage("§8[§7XAuth§8] §aAccount for §f" + targetName + " §ahas been reset. They must re-register on next join.");
        return true;
    }
}