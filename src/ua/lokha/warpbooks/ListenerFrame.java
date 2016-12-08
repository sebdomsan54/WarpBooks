package ua.lokha.warpbooks;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ListenerFrame implements Listener {
    private WarpBooksPlugin warpBooksPlugin;

    public ListenerFrame(WarpBooksPlugin warpBooksPlugin) {
        this.warpBooksPlugin = warpBooksPlugin;
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        if (!event.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            return;
        }

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        ItemStack item = itemFrame.getItem();
        if (item != null && !item.getType().equals(Material.AIR)) {
            String name = item.getItemMeta().getDisplayName();
            if (name != null) {
                Material type = item.getType();
                if (type.equals(Material.WRITTEN_BOOK)) {
                    BookMeta bookMeta = ((BookMeta) item.getItemMeta());
                    if (bookMeta.getPageCount() > 0) {
                        String page1 = bookMeta.getPage(1);
                        if (page1.startsWith(warpBooksPlugin.codeTeleportBook)) {
                            event.setCancelled(true);
                            if (!event.getPlayer().hasPermission("Transportsscrolls.frame.use")) {
                                Message.no_perm.send(event.getPlayer());
                            } else {
                                warpBooksPlugin.teleport(event.getPlayer(), page1, new TeleporterFrame(itemFrame, item, event.getPlayer()));
                            }
                        }
                    }
                }
            }
        }
    }
}
