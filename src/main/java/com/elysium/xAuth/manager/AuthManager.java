package com.elysium.xAuth.manager;

import com.elysium.xAuth.XAuth;
import com.elysium.xAuth.storage.PlayerDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AuthManager {
    private final XAuth plugin;
    private final PlayerDataStorage storage;
    private final Set<UUID> loggedIn = new HashSet<>();
    private final Map<UUID, Integer> failedAttempts = new HashMap<>();
    private final Map<UUID, Long> lockoutUntil = new HashMap<>();
    private final Map<UUID, BukkitTask> loginTimeoutTasks = new HashMap<>();
    private final Map<UUID, PendingAuthCommand> pendingAuthCommands = new HashMap<>();

    public AuthManager(XAuth plugin, PlayerDataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    private String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean isRegistered(UUID uuid) {
        return storage.isRegistered(uuid);
    }

    public void register(UUID uuid, String password) {
        storage.registerPlayer(uuid, hash(password));
    }

    public boolean login(Player player, String password) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long lockedUntil = lockoutUntil.getOrDefault(uuid, 0L);

        if (now < lockedUntil) {
            long secondsLeft = Math.max(1L, (lockedUntil - now + 999L) / 1000L);
            player.sendMessage(plugin.getPrefix() + " §cYou are locked out for " + secondsLeft + " more seconds.");
            return false;
        }

        String stored = storage.getStoredPassword(uuid);
        boolean passwordMatches;
        try {
            passwordMatches = stored != null && BCrypt.checkpw(password, stored);
        } catch (IllegalArgumentException e) {
            storage.deletePlayer(uuid);
            logout(uuid);
            player.kickPlayer(plugin.getPrefix() + " §cYour account was reset due to a security upgrade.\n§fPlease rejoin and register again.");
            return false;
        }

        if (passwordMatches) {
            loggedIn.add(uuid);
            failedAttempts.remove(uuid);
            lockoutUntil.remove(uuid);
            cancelLoginTimeout(uuid);
            player.sendTitle(
                    "§7§lWelcome Back",
                    "§7Logged in as §f§l" + player.getName(),
                    10, 80, 20
            );
            player.sendMessage(plugin.getPrefix() + " §7Welcome back, §f" + player.getName() + "§7!");
            return true;
        }

        int attempts = failedAttempts.getOrDefault(uuid, 0) + 1;
        failedAttempts.put(uuid, attempts);
        if (attempts >= 5) {
            lockoutUntil.put(uuid, now + 60_000L);
            failedAttempts.remove(uuid);
            player.sendMessage(plugin.getPrefix() + " §cToo many failed attempts. You are locked out for 60 seconds.");
        }
        return false;
    }

    public void forceLogin(Player player) {
        UUID uuid = player.getUniqueId();
        loggedIn.add(uuid);
        failedAttempts.remove(uuid);
        lockoutUntil.remove(uuid);
        cancelLoginTimeout(uuid);
    }

    public void logout(UUID uuid) {
        loggedIn.remove(uuid);
    }

    public boolean isLoggedIn(UUID uuid) {
        return loggedIn.contains(uuid);
    }

    public boolean isLockedOut(UUID uuid) {
        return System.currentTimeMillis() < lockoutUntil.getOrDefault(uuid, 0L);
    }

    public void scheduleLoginTimeout(Player player) {
        UUID uuid = player.getUniqueId();
        cancelLoginTimeout(uuid);

        long timeoutSeconds = plugin.getConfig().getLong("login-timeout", 60L);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null && onlinePlayer.isOnline() && !isLoggedIn(uuid)) {
                onlinePlayer.kickPlayer(plugin.getPrefix() + " §cYou took too long to login. Please rejoin.");
            }
            loginTimeoutTasks.remove(uuid);
        }, timeoutSeconds * 20L);

        loginTimeoutTasks.put(uuid, task);
    }

    public void cancelLoginTimeout(UUID uuid) {
        BukkitTask task = loginTimeoutTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    public void clearLoginAttempts(UUID uuid) {
        // Failed-login state is session-scoped, so quitting resets brute-force counters.
        failedAttempts.remove(uuid);
        lockoutUntil.remove(uuid);
        pendingAuthCommands.remove(uuid);
    }

    public String getPrefix() {
        return plugin.getPrefix();
    }

    public void storePendingAuthCommand(UUID uuid, String command, String[] args) {
        pendingAuthCommands.put(uuid, new PendingAuthCommand(command, args));
    }

    public String[] consumePendingAuthCommand(UUID uuid, String command) {
        PendingAuthCommand pending = pendingAuthCommands.remove(uuid);
        if (pending == null || !pending.command().equals(command)) {
            return null;
        }
        return pending.args();
    }

    private record PendingAuthCommand(String command, String[] args) {
    }
}
