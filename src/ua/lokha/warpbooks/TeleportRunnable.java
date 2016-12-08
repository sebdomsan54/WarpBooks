package ua.lokha.warpbooks;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportRunnable extends BukkitRunnable {
    private PlayerData playerData;
    private Location pos;
    private BookData bookData;
    public Teleporter teleporter;

    public TeleportRunnable(PlayerData playerData, BookData bookData, Teleporter teleporter) {
        this.playerData = playerData;
        this.bookData = bookData;
        this.teleporter = teleporter;
        playerData.teleport = this;
        this.pos = playerData.player.getLocation();
    }

    @Override
    public void run() {
        playerData.teleport = null;
        if (this.playerData.player.isDead()) {
            return;
        }
        int amount = teleporter.getMoney();
        if (amount < bookData.getCost()) {
            Message.missing_items.send(playerData.player, "%count%", String.valueOf(bookData.getCost() - amount));
        } else {
            int slot = this.teleporter.getSlot();
            ItemStack item = this.teleporter.getTeleporter();
            if (slot == -1) {
                Message.teleporter_not_fount.send(playerData.player);
            } else {
                Location location = WarpBooksPlugin.plugin.fixTeleportLocation(bookData.getLocation());
                if (location == null) {
                    Message.bad_location.send(playerData.player);
                } else {
                    if (!teleporter.hasSpongeAllow(location)) {
                        Message.sponge_deny_teleport.send(playerData.player);
                    } else {
                        teleporter.takeMoney(bookData.getCost());
                        bookData.nowLastUser();
                        if (bookData.getDurability() != -1) {
                            if (bookData.getDurability() <= 0) {
                                this.teleporter.removeItem(item);
                                Message.teleported_last.send(playerData.player);
                            } else {
                                bookData.setData(item);
                                this.teleporter.setItem(item, slot);
                                Message.teleported.send(playerData.player, "%count%", String.valueOf(bookData.getDurability()));
                            }
                        } else {
                            bookData.setData(item);
                            Message.teleported_unlimited.send(playerData.player, "%count%", String.valueOf(bookData.getDurability()));
                        }
                        playerData.player.teleport(location);
                    }
                }
            }
        }
    }

    public void cancelMove() {
        Message.cancel_telepor_move.send(this.playerData.player);
        this.cancel();
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        playerData.teleport = null;
        super.cancel();
    }

    public Location getPos() {
        return pos;
    }
}
