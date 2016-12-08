package ua.lokha.warpbooks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.Collections;

public class BookData {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int durability;
    private int cost;
    private int cooldown;
    private int warmup;
    private long lastUse;

    public BookData() {}

    public BookData(Location loc, int durability, int cost, int cooldown, int warmup) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.durability = durability;
        this.cost = cost;
        this.cooldown = cooldown;
        this.warmup = warmup;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public void nowLastUser() {
        if (durability != -1) {
            this.durability--;
        }
        this.lastUse = System.currentTimeMillis();
    }

    public int calculateCooldown(){
        return (int) (this.cooldown - ((System.currentTimeMillis() - this.lastUse) / 1000));
    }

    public int getDurability() {
        return durability;
    }

    public void teleport(PlayerData player, Teleporter teleporter) {
        Message.teleport_later.send(player.player, "%second%", String.valueOf(warmup / 20));
        player.teleport = new TeleportRunnable(player, this, teleporter);
        player.teleport.runTaskLater(WarpBooksPlugin.plugin, warmup);
    }


    public void setData(ItemStack item) {
        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        bookMeta.setLore(Arrays.asList(Message.teleported_book_lore.get(
                "%count%", String.valueOf(this.durability),
                "%cost%", String.valueOf(this.cost),
                "%cooldown%", String.valueOf(this.cooldown),
                "%warmup%", String.valueOf(this.warmup / 20)
                ).split("\n")));
        bookMeta.setPages(Collections.singletonList(WarpBooksPlugin.plugin.codeTeleportBook + WarpBooksPlugin.plugin.toBookData(this)));
        item.setItemMeta(bookMeta);
    }

    public void setDuramility(int durability) {
        this.durability = durability;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setWarmup(int warmup) {
        this.warmup = warmup;
    }
}
