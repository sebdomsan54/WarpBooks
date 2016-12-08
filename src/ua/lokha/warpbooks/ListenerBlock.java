package ua.lokha.warpbooks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

class ListenerBlock implements Listener {
    private WarpBooksPlugin plugin;

    ListenerBlock(WarpBooksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityInteractEvent(EntityInteractEvent event) {
        Block down = event.getBlock().getRelative(BlockFace.DOWN);
        if (down.getType().equals(plugin.block)) {
            Block inv = down.getRelative(BlockFace.DOWN);
            BlockState blockState = inv.getState();
            if (blockState instanceof InventoryHolder) {
                InventoryHolder holder = (InventoryHolder) blockState;
                int slot = 0;
                for (ItemStack item : holder.getInventory().getContents()) {
                    if (item == null || !item.getType().equals(Material.WRITTEN_BOOK)) {
                        continue;
                    }

                    BookMeta bookMeta = (BookMeta) item.getItemMeta();
                    if (bookMeta.getPageCount() > 0) {
                        String page1 = bookMeta.getPage(1);
                        if (page1.startsWith(plugin.codeTeleportBook)) {
                            BookData bookData = plugin.getBookData(page1.substring(plugin.codeTeleportBook.length()));
                            int amount = Util.getAmount(holder.getInventory(), plugin.price);
                            if (amount >= bookData.getCost()) {
                                bookData.nowLastUser();
                                Util.removeItems(holder.getInventory(), plugin.price, bookData.getCost());
                                bookData.setData(item);
                                holder.getInventory().setItem(slot, item);
                                event.getEntity().teleport(bookData.getLocation());
                                break;
                            }
                        }
                    }
                    slot++;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL) && event.hasBlock()) {
            Block down = event.getClickedBlock().getRelative(BlockFace.DOWN);
            if (down.getType().equals(plugin.block)) {
                PlayerData playerData = PlayerData.datas.get(event.getPlayer());
                if (playerData.teleport == null && playerData.hasLastPhisic() && event.getPlayer().hasPermission("Transportsscrolls.telepad.use")) {
                    Block inv = down.getRelative(BlockFace.DOWN);
                    BlockState blockState = inv.getState();
                    if (blockState instanceof InventoryHolder) {
                        InventoryHolder holder = (InventoryHolder) blockState;
                        for (ItemStack item : holder.getInventory().getContents()) {
                            if (item == null || !item.getType().equals(Material.WRITTEN_BOOK)) {
                                continue;
                            }

                            BookMeta bookMeta = (BookMeta) item.getItemMeta();
                            if (bookMeta.getPageCount() > 0) {
                                String page1 = bookMeta.getPage(1);
                                if (page1.startsWith(plugin.codeTeleportBook)) {
                                    event.setUseItemInHand(Event.Result.DENY);
                                    event.setUseInteractedBlock(Event.Result.DENY);
                                    plugin.teleport(event.getPlayer(), page1, new TeleporterBlock(item, event.getPlayer(), inv));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
