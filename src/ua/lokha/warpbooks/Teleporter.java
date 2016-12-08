package ua.lokha.warpbooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Teleporter {
    ItemStack getTeleporter();
    int getSlot();
    void setItem(ItemStack item, int slot);
    void removeItem(ItemStack item);
    Player getPlayer();

    default int getMoney() {
        return Util.getAmount(this.getPlayer().getInventory(), WarpBooksPlugin.plugin.price);
    }

    default void takeMoney(int cost) {
        Util.removeItems(this.getPlayer().getInventory(), WarpBooksPlugin.plugin.price, cost);
    }

    boolean hasSpongeAllow(Location to);
}
