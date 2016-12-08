package ua.lokha.warpbooks;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeleporterFrame implements Teleporter {
    private ItemFrame itemFrame;
    private ItemStack item;
    private Player player;

    public TeleporterFrame(ItemFrame itemFrame, ItemStack item, Player player) {
        this.itemFrame = itemFrame;
        this.item = item.clone();
        this.player = player;
    }

    @Override
    public ItemStack getTeleporter() {
        return item;
    }

    @Override
    public int getSlot() {
        return this.item.equals(this.itemFrame.getItem()) ? 0 : -1;
    }

    @Override
    public void setItem(ItemStack item, int slot) {
        this.itemFrame.setItem(item);
    }

    @Override
    public void removeItem(ItemStack item) {
        this.itemFrame.setItem(null);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean hasSpongeAllow(Location to) {
        if (Util.hasBlockSponge(player.getInventory())) {
            return false;
        }
        if (Util.hasBlockSponge(to)) {
            return false;
        }
        if (Util.hasBlockSponge(itemFrame.getLocation())) {
            return false;
        }

        return true;
    }
}
