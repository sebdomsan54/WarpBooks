package ua.lokha.warpbooks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class TeleporterBlock implements Teleporter {
    private ItemStack item;
    private Player player;
    private Block block;

    public TeleporterBlock(ItemStack item, Player player, Block block) {
        this.item = item;
        this.player = player;
        this.block = block;
    }

    @Override
    public ItemStack getTeleporter() {
        return item;
    }

    @Override
    public int getSlot() {
        BlockState blockState = block.getState();
        if (blockState instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) blockState;
            return Util.getSlot(holder.getInventory(), this.item);
        }
        return -1;
    }

    @Override
    public void setItem(ItemStack item, int slot) {
        BlockState blockState = block.getState();
        if (blockState instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) blockState;
            holder.getInventory().setItem(slot, item);
        }
    }

    @Override
    public void removeItem(ItemStack item) {
        BlockState blockState = block.getState();
        if (blockState instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) blockState;
            Util.removeItems(holder.getInventory(), item, item.getAmount());
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int getMoney() {
        int count = Util.getAmount(this.getPlayer().getInventory(), WarpBooksPlugin.plugin.price);
        BlockState blockState = block.getState();
        if (blockState instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) blockState;
            count += Util.getAmount(holder.getInventory(), WarpBooksPlugin.plugin.price);
        }
        return count;
    }

    @Override
    public void takeMoney(int cost) {
        int remove = 0;
        BlockState blockState = block.getState();
        if (blockState instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) blockState;
            remove = Util.getAmount(holder.getInventory(), WarpBooksPlugin.plugin.price);
            Util.removeItems(holder.getInventory(), WarpBooksPlugin.plugin.price, cost);
        }

        Util.removeItems(player.getInventory(), WarpBooksPlugin.plugin.price, cost - remove);
    }

    @Override
    public boolean hasSpongeAllow(Location to) {
        if (Util.hasBlockSponge(to)) {
            return false;
        }

        return !(Util.hasBlockSponge(player.getInventory()) || Util.hasBlockSponge(block.getLocation()));
    }
}
