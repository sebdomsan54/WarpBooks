package ua.lokha.warpbooks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ListenerPlayer implements Listener {
    private WarpBooksPlugin warpBooksPlugin;

    public ListenerPlayer(WarpBooksPlugin warpBooksPlugin) {
        this.warpBooksPlugin = warpBooksPlugin;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        if (event.hasItem()) {
            ItemStack item = event.getItem();
            String name = item.getItemMeta().getDisplayName();
            if (name != null) {
                Material type = item.getType();
                if (type.equals(Material.BOOK)) {
                    if (name.startsWith(warpBooksPlugin.codeCraftBook)) {
                        Player player = event.getPlayer();
                        Util.removeItems(player.getInventory(), item, 1);
                        ItemStack written = warpBooksPlugin.createTeleportBy(player.getLocation());
                        if (item.getAmount() == 1) {
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), written);
                        } else {
                            player.getInventory().addItem(written);
                        }
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        Message.teleported_book_created.send(player);
                    }
                } else if (type.equals(Material.WRITTEN_BOOK)) {
                    BookMeta bookMeta = ((BookMeta) item.getItemMeta());
                    if (bookMeta.getPageCount() > 0) {
                        String page1 = bookMeta.getPage(1);
                        if (page1.startsWith(warpBooksPlugin.codeTeleportBook)) {
                            event.setUseItemInHand(Event.Result.DENY);
                            event.setUseInteractedBlock(Event.Result.DENY);

                            warpBooksPlugin.teleport(event.getPlayer(), page1, new TeleporterPlayer(event.getPlayer(), item));
                        }
                    }
                }
            }
        }
    }
}
