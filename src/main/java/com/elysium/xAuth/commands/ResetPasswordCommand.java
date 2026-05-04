package com.elysium.xAuth.commands;

import com.elysium.xAuth.manager.AuthManager;
import com.elysium.xAuth.storage.PlayerDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
            sender.sendMessage(auth.getPrefix() + " §cYou do not have permission to do this.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(auth.getPrefix() + " §cUsage: §f/xreset <player>");
            return true;
        }

        String targetName = args[0];
        Player online = Bukkit.getPlayerExact(targetName);
        UUID targetUuid;

        if (online != null) {
            targetUuid = online.getUniqueId();
        } else {
            // This may perform a blocking web request on online-mode servers.
            targetUuid = Bukkit.getOfflinePlayer(targetName).getUniqueId();
        }

        if (!auth.isRegistered(targetUuid)) {
            sender.sendMessage(auth.getPrefix() + " §cPlayer §f" + targetName + " §cis not registered.");
            return true;
        }

        storage.deletePlayer(targetUuid);

        if (online != null && online.isOnline()) {
            auth.logout(online.getUniqueId());
            auth.cancelLoginTimeout(online.getUniqueId());
            online.kickPlayer(auth.getPrefix() + " §eYour account has been reset.\n§fPlease rejoin and register again.");
        }

        sender.sendMessage(auth.getPrefix() + " §aAccount for §f" + targetName + " §ahas been reset. They must re-register on next join.");
        return true;
    }
}
