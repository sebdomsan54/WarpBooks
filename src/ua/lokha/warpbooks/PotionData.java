package ua.lokha.warpbooks;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PotionData {
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int cost;

    public PotionData() {
    }

    public PotionData(BookData bookData) {
        Location loc = bookData.getLocation();
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.cost = bookData.getCost();
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public int getCost() {
        return cost;
    }
}
