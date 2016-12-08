package ua.lokha.warpbooks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static String color(String line) {
        return line.replace("&", "§");
    }

    public static List<String> color(List<String> lines) {
        return lines.stream().map(Util::color).collect(Collectors.toList());
    }

    public static void setDesc(ItemStack item, String description) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        String[] desc = description.split("\n");
        itemMeta.setDisplayName(desc[0]);
        itemMeta.setLore(Arrays.asList(desc).subList(1, desc.length));
        item.setItemMeta(itemMeta);
    }

    /**
     * Посчитать, сколько таких предметов в инвентаре
     * @param inventory
     * @param item
     * @return
     */
    public static int getAmount(Inventory inventory, ItemStack item) {
        ItemStack[] items = inventory.getContents();
        int search = 0;
        for (ItemStack i : items) {
            if (i == null)
                continue;
            if (i.isSimilar(item)) {
                search += i.getAmount();
            }
        }
        return search;
    }

    public static boolean hasBlockSponge(Inventory inventory) {
        return inventory.contains(WarpBooksPlugin.plugin.sponge);
    }

    public static boolean hasBlockSponge(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        World world = loc.getWorld();
        int radius = WarpBooksPlugin.plugin.radiusSponge;

        for (int xx = x - radius; xx <= x + radius; xx++) {
            for (int yy = y - radius; yy <= y + radius; yy++) {
                for (int zz = z - radius; zz <= z + radius; zz++) {
                    if (world.getBlockAt(xx, yy, zz).getType().equals(WarpBooksPlugin.plugin.sponge)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<ItemStack> removeItems(Inventory inventory, ItemStack object, int amount) {
        if (amount <= 0) {
            return Collections.emptyList();
        }
        List<ItemStack> removed = new ArrayList<>();
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && item.isSimilar(object)) {
                int am = item.getAmount();
                if (amount > am) {
                    amount -= am;
                    removed.add(inventory.getItem(i));
                    inventory.clear(i);
                } else if (amount < am) {
                    item.setAmount(am - amount);
                    ItemStack old = inventory.getItem(i).clone();
                    old.setAmount(amount);
                    removed.add(old);
                    inventory.setItem(i, item);
                    return removed;
                } else {
                    removed.add(inventory.getItem(i));
                    inventory.clear(i);
                    return removed;
                }
            }
        }
        return removed;
    }

    public static int getSlot(Inventory inventory, ItemStack teleporter) {
        int slot = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.equals(teleporter)) {
                return slot;
            }
            slot++;
        }
        return -1;
    }

    public static void setLore(ItemStack item, String description) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setLore(Arrays.asList(description.split("\n")));
        item.setItemMeta(itemMeta);
    }

    public static void setName(ItemStack item, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
    }
}
