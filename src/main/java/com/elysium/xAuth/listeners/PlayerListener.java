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

        if (!auth.isRegistered(p.getName())) {
            p.sendMessage("§8[§7XAuth§8] §eWelcome! You must register to play.");
            p.sendMessage("§8[§7XAuth§8] §fUse: §e/register <password> <confirmPassword>");
        } else {
            p.sendMessage("§8[§7XAuth§8] §eWelcome back! Please login.");
            p.sendMessage("§8[§7XAuth§8] §fUse: §e/login <password>");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        auth.logout(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent e) {
        if (auth.isLoggedIn(e.getPlayer().getUniqueId())) return;
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() ||
                e.getFrom().getBlockY() != e.getTo().getBlockY() ||
                e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§8[§7XAuth§8] §cYou must login before chatting!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (auth.isLoggedIn(e.getPlayer().getUniqueId())) return;
        String cmd = e.getMessage().toLowerCase();
        if (!cmd.startsWith("/login") && !cmd.startsWith("/register")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§8[§7XAuth§8] §cYou can only use /login or /register right now!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        if (!auth.isLoggedIn(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!auth.isLoggedIn(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        if (!auth.isLoggedIn(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!auth.isLoggedIn(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDropItem(PlayerDropItemEvent e) {
        if (!auth.isLoggedIn(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }
}