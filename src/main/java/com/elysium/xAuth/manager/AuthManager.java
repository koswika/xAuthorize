package com.elysium.xAuth.manager;

import com.elysium.xAuth.XAuth;
import com.elysium.xAuth.storage.PlayerDataStorage;
import org.bukkit.entity.Player;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthManager {
    private final XAuth plugin;
    private final PlayerDataStorage storage;
    private final Set<UUID> loggedIn = new HashSet<>();

    public AuthManager(XAuth plugin, PlayerDataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public boolean isRegistered(String username) {
        return storage.isRegistered(username);
    }

    public void register(String username, String password) {
        storage.registerPlayer(username, hash(password));
    }

    public boolean login(Player player, String password) {
        String stored = storage.getStoredPassword(player.getName());
        if (stored != null && stored.equals(hash(password))) {
            loggedIn.add(player.getUniqueId());
            player.sendTitle(
                    "§7§lWelcome Back",
                    "§7Logged in as §f§l" + player.getName(),
                    10, 80, 20
            );
            player.sendMessage("§8[§7XAuth§8] §7Welcome back, §f" + player.getName() + "§7!");
            return true;
        }
        return false;
    }

    public void forceLogin(Player player) {
        loggedIn.add(player.getUniqueId());
    }

    public void logout(UUID uuid) {
        loggedIn.remove(uuid);
    }

    public boolean isLoggedIn(UUID uuid) {
        return loggedIn.contains(uuid);
    }
}