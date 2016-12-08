package ua.lokha.warpbooks;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ListenerPotion implements Listener {
    private WarpBooksPlugin plugin;
    private TypeAdapter<PotionData> adapter = new Gson().getAdapter(PotionData.class);

    public ListenerPotion(WarpBooksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(CraftItemEvent event) {

    }

    @EventHandler
    public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();
        if (result != null && result.getType().equals(Material.SPLASH_POTION)) {
            ItemMeta potion = result.getItemMeta();
            if (potion.getDisplayName().equals("%teleport_potion%")) {
                boolean cancel = true;
                for (ItemStack item : inventory.getMatrix()) {
                    if (item == null || !item.getType().equals(Material.WRITTEN_BOOK)) {
                        continue;
                    }

                    BookMeta bookMeta = (BookMeta) item.getItemMeta();
                    if (bookMeta.getPageCount() > 0) {
                        String page1 = bookMeta.getPage(1);
                        if (page1.startsWith(plugin.codeTeleportBook)) {
                            BookData bookData = plugin.getBookData(page1.substring(plugin.codeTeleportBook.length()));
                            try {
                                Util.setDesc(result, Message.teleported_potion.get(
                                        "%name%", item.getItemMeta().getDisplayName(),
                                        "%cost%", String.valueOf(bookData.getCost())
                                ) + plugin.codeTeleportPotion + toCode(adapter.toJson(new PotionData(bookData))));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            inventory.setResult(result);
                            cancel = false;
                            break;
                        }
                    }
                }
                if (cancel) {
                    inventory.setResult(null);
                }
            }
        }
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null || !result.getType().equals(Material.SPLASH_POTION)) {
            return;
        }

        List<String> lore = result.getItemMeta().getLore();
        if (lore.isEmpty() || !lore.get(lore.size() - 1).contains(plugin.codeTeleportPotion)) {
            return;
        }

        if (!event.getWhoClicked().hasPermission("Transportsscrolls.craft")) {
            event.setCancelled(true);
            Message.no_perm.send(event.getWhoClicked());
        } else {
            Message.crafted_potion_crafted.send(event.getWhoClicked());
        }
    }

    @EventHandler
    public void onPotionSplashEvent(PotionSplashEvent event) {
        ItemStack item = event.getPotion().getItem();
        if (item == null || !item.getType().equals(Material.SPLASH_POTION)) {
            return;
        }
        List<String> lore = item.getItemMeta().getLore();
        if (lore.isEmpty() || !lore.get(lore.size() - 1).contains(plugin.codeTeleportPotion)) {
            return;
        }

        String[] data = lore.get(lore.size() - 1).split(plugin.codeTeleportPotion);
        PotionData potionData = null;
        try {
            potionData = adapter.fromJson(toJson(data[data.length - 1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (potionData == null) {
            return;
        }
        if (!(event.getPotion().getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) event.getPotion().getShooter();
        Location loc = WarpBooksPlugin.plugin.fixTeleportLocation(potionData.getLocation());
        if (loc == null) {
            Message.bad_location.send(shooter);
            return;
        }

        if (Util.hasBlockSponge(event.getPotion().getLocation())) {
            Message.sponge_deny_teleport.send(shooter);
            return;
        }

        int r = plugin.radiusPotionEffect;
        boolean messNoMoney = false;
        for (Entity entity : event.getPotion().getNearbyEntities(r, r, r)) {
            if (entity.getType().equals(EntityType.PLAYER)) {
                if (Util.hasBlockSponge(((Player) entity).getInventory())) {
                    Message.sponge_deny_teleport.send(entity);
                } else {
                    ItemStack price = WarpBooksPlugin.plugin.price;
                    int amount = Util.getAmount(shooter.getInventory(), price);
                    if (amount < potionData.getCost()) {
                        if (!messNoMoney) {
                            Message.missing_items.send(shooter, "%count%", String.valueOf(potionData.getCost() - amount));
                            messNoMoney = true;
                        }
                    } else {
                        Util.removeItems(shooter.getInventory(), price, potionData.getCost());
                        entity.teleport(loc);
                        Message.teleported_unlimited.send(entity);
                    }
                }
            } else if (entity instanceof LivingEntity) {
                entity.teleport(loc);
            }
        }
    }

    public static String toCode(String json) {
        char[] value = json.toCharArray();
        char[] code = new char[value.length * 2];
        Arrays.fill(code, '§');
        int j = 0;
        for (int i = 1; i < code.length; i += 2) {
            code[i] = value[j++];
        }
        return new String(code);
    }

    public static String toJson(String code) {
        char[] value = code.toCharArray();
        char[] json = new char[value.length / 2];
        int j = 0;
        for (int i = 1; i < value.length; i += 2) {
            json[j++] = value[i];
        }
        return new String(json);
    }
}
