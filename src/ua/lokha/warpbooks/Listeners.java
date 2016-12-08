package ua.lokha.warpbooks;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class Listeners implements Listener {
    private WarpBooksPlugin warpBooksPlugin;

    Listeners(WarpBooksPlugin warpBooksPlugin) {
        this.warpBooksPlugin = warpBooksPlugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        PlayerData.datas.put(event.getPlayer(), new PlayerData(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        PlayerData.datas.remove(event.getPlayer());
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        if (event.getRecipe().getResult().equals(warpBooksPlugin.craftedBook)) {
            if (!event.getWhoClicked().hasPermission("Transportsscrolls.craft")) {
                event.setCancelled(true);
                Message.no_perm.send(event.getWhoClicked());
            } else {
                Message.crafted_book_crafted.send(event.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Location t = event.getTo();
        Location f = event.getFrom();
        if (t.getBlockX() != f.getBlockX() || t.getBlockY() != f.getBlockY() || t.getBlockZ() != f.getBlockZ()) {
            TeleportRunnable teleport = PlayerData.datas.get(event.getPlayer()).teleport;
            if (teleport != null) {
                teleport.cancelMove();
            }
        }
    }
}
