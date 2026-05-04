package com.elysium.xAuth.listeners;

import com.elysium.xAuth.XAuth;
import com.elysium.xAuth.manager.AuthManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private final AuthManager auth;
    private final XAuth plugin;

    public PlayerListener(AuthManager auth, XAuth plugin) {
        this.auth = auth;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        auth.scheduleLoginTimeout(p);

        if (!auth.isRegistered(p.getUniqueId())) {
            p.sendMessage(plugin.getPrefix() + " §eWelcome! You must register to play.");
            p.sendMessage(plugin.getPrefix() + " §fUse: §e/register <password> <confirmPassword>");
        } else {
            p.sendMessage(plugin.getPrefix() + " §eWelcome back! Please login.");
            p.sendMessage(plugin.getPrefix() + " §fUse: §e/login <password>");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        java.util.UUID uuid = e.getPlayer().getUniqueId();
        auth.logout(uuid);
        auth.cancelLoginTimeout(uuid);
        auth.clearLoginAttempts(uuid);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (auth.isLoggedIn(e.getPlayer().getUniqueId())) return;
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() ||
                e.getFrom().getBlockY() != e.getTo().getBlockY() ||
                e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.getPrefix() + " §cYou must login before chatting!");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        String cmd = message.toLowerCase();
        if (isSensitiveAuthCommand(cmd)) {
            auth.storePendingAuthCommand(e.getPlayer().getUniqueId(), getCommandName(cmd), getCommandArgs(message));
            e.setMessage(censorAuthCommand(message));
        }

        if (auth.isLoggedIn(e.getPlayer().getUniqueId())) return;
        if (!isAllowedAuthCommand(cmd)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.getPrefix() + " §cYou can only use /login or /register right now!");
        }
    }

    private boolean isSensitiveAuthCommand(String message) {
        return startsWithCommand(message, "/login") || startsWithCommand(message, "/register");
    }

    private boolean isAllowedAuthCommand(String message) {
        return startsWithCommand(message, "/login") || startsWithCommand(message, "/register");
    }

    private boolean startsWithCommand(String message, String command) {
        return message.equals(command) || message.startsWith(command + " ");
    }

    private String censorAuthCommand(String message) {
        String[] parts = message.trim().split("\\s+");
        if (parts.length == 0) {
            return message;
        }

        StringBuilder censored = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            censored.append(" ****");
        }
        return censored.toString();
    }

    private String getCommandName(String message) {
        return message.trim().split("\\s+", 2)[0].substring(1);
    }

    private String[] getCommandArgs(String message) {
        String[] parts = message.trim().split("\\s+");
        if (parts.length <= 1) {
            return new String[0];
        }

        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        return args;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        if (!auth.isLoggedIn(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!auth.isLoggedIn(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        if (!auth.isLoggedIn(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!auth.isLoggedIn(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }
}
