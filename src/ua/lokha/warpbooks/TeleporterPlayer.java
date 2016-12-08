package ua.lokha.warpbooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeleporterPlayer implements Teleporter {

    private Player player;
    private ItemStack item;

    public TeleporterPlayer(Player player, ItemStack item) {
        this.player = player;
        this.item = item.clone();
    }

    @Override
    public ItemStack getTeleporter() {
        return item;
    }

    @Override
    public int getSlot() {
        return Util.getSlot(player.getInventory(), this.item);
    }

    @Override
    public void setItem(ItemStack item, int slot) {
        player.getInventory().setItem(slot, item);
    }

    @Override
    public void removeItem(ItemStack item) {
        Util.removeItems(this.player.getInventory(), item, item.getAmount());
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean hasSpongeAllow(Location to) {
        if (Util.hasBlockSponge(to)) {
            return false;
        }

        return !(Util.hasBlockSponge(player.getInventory()) || Util.hasBlockSponge(player.getLocation()));
    }
}
