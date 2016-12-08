package ua.lokha.warpbooks;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WarpBooksPlugin extends JavaPlugin {

    static WarpBooksPlugin plugin;

    final ItemStack craftedBook = new ItemStack(Material.BOOK);
    final ItemStack teleportedBook = new ItemStack(Material.WRITTEN_BOOK);
    final ItemStack teleportedPotion = new ItemStack(Material.SPLASH_POTION);

    final String codeCraftBook = "§☭";
    final String codeTeleportBook = "§☀";
    final String codeTeleportPotion = "§♣";

    private TypeAdapter<BookData> bookAdapter = new Gson().getAdapter(BookData.class);

    private Config config = new Config(new File(this.getDataFolder() + File.separator + "config.yml"));

    ItemStack price;
    private int priceAmountDefault;
    private int cooldownDefault;
    private int durabilityDefault;
    private int warmupDefault;

    private ArrayIds notCollision;
    private ArrayIds allowStand;

    Material sponge;
    int radiusSponge;
    int radiusPotionEffect;
    Material block;

    public WarpBooksPlugin() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.config.reload();
        this.registerEvents();
        this.registerCommand();
        this.loadMessage();
        this.loadRecipe();
        this.loadRecipePotion();
        this.loadTeleportedBook();
        this.loadParameters();
        this.loadNotCollision();
        this.loadSettings();
    }

    private void loadSettings() {
        //133
        this.block = Material.getMaterial(config.getOrSet("block-teleporter-id", 133));
        this.radiusPotionEffect = config.getOrSet("radius-potion-effect", 2);
        this.radiusSponge = config.getOrSet("radius-sponge-blocks", 3);
        this.sponge = Material.getMaterial(config.getOrSet("songe-id", 19));
    }

    private void loadNotCollision() {
        this.notCollision = new ArrayIds(config.getOrSet("not-collision", Arrays.asList(
                142, 141, 104, 105, 207, 59, 83, 115, 0, 8, 9, 6, 32, 31, 38, 37, 39, 40, 50, 65, 78, 106, 171, 175, 176, 69,
                70, 72, 76, 75, 77, 143, 147, 148, 167, 96, 55, 93, 94, 149, 150, 27, 28, 66, 157, 63, 68
        )));

        this.allowStand = new ArrayIds(config.getOrSet("allow-stand", Arrays.asList(
                1, 2, 3, 4, 5, 7, 8, 9, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 33, 35,
                41, 42, 43, 44, 45, 46, 47, 48, 49, 52, 53, 54, 56, 57, 58, 60, 61, 62, 67, 73, 74, 79, 80, 82, 84, 86, 87,
                88, 89, 91, 92, 95, 97, 98, 99, 100, 103, 108, 109, 110, 111, 112, 114, 116, 118, 121, 123, 124, 125, 126,
                128, 129, 130, 133, 134, 135, 136, 137, 138, 146, 151, 152, 153, 154, 155, 156, 158, 159, 160, 161, 162, 163,
                164, 165, 166, 167, 168, 169, 170, 172, 173
        )));
    }

    private void registerCommand() {
        this.getCommand("tpbookedit").setExecutor(new Command(this));
    }

    private void loadParameters() {
        try {
            String[] data = config.getOrSet("price-default", "388:5").split(":");
            this.priceAmountDefault = Integer.parseInt(data[1]);
            this.price = new ItemStack(
                    Material.getMaterial(Integer.parseInt(data[0])),
                    this.priceAmountDefault,
                    (short) 0
            );
        } catch (Exception e) {
            this.getLogger().severe("Error load 'price', format: id:amount");
            e.printStackTrace();
        }
        this.cooldownDefault = config.getOrSet("cooldown-default-seconds", 5);
        this.durabilityDefault = config.getOrSet("durability-default", 10);
        this.warmupDefault = config.getInt("warmup-default-ticks", 20*5);
    }

    private void loadTeleportedBook() {
        ItemMeta itemMeta = this.teleportedBook.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        teleportedBook.setItemMeta(itemMeta);
    }

    public BookData getBookData(String data) {
        try {
            return bookAdapter.fromJson(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toBookData(BookData data) {
        try {
            return bookAdapter.toJson(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack createTeleportBy(Location loc) {
        ItemStack item = this.teleportedBook.clone();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setDisplayName(Message.teleported_book_name.get(
                "%x%", String.valueOf(loc.getBlockX()),
                "%y%", String.valueOf(loc.getBlockY()),
                "%z%", String.valueOf(loc.getBlockZ())
        ));
        item.setItemMeta(itemMeta);
        BookData bookData = new BookData(loc, this.durabilityDefault, this.priceAmountDefault, this.cooldownDefault, this.warmupDefault);
        bookData.setData(item);
        return item;
    }

    private void loadRecipe() {
        Util.setDesc(craftedBook, codeCraftBook + Message.crafted_book.getValue());

        ShapedRecipe recipe = new ShapedRecipe(craftedBook);
        recipe.shape(config.getOrSet("recipe.book", Arrays.asList("***","*%*","***")).toArray(new String[0]));
        for (String ingredient : config.getOrSet("recipe.data-book", Arrays.asList("*:340", "%:368"))) {
            try {
                String[] data = ingredient.split(":");
                recipe.setIngredient(
                        data[0].charAt(0),
                        Material.getMaterial(Integer.parseInt(data[1]))
                );
            } catch (Exception e) {
                this.getLogger().severe("Error load recipe '" + ingredient + "', format: 'char:id'");
                e.printStackTrace();
            }
        }
        Bukkit.getServer().addRecipe(recipe);
    }

    private void loadRecipePotion() {
        Util.setName(teleportedPotion, "%teleport_potion%");

        ShapedRecipe recipe = new ShapedRecipe(teleportedPotion);
        recipe.shape(config.getOrSet("recipe.potion", Arrays.asList("***","*%*","***")).toArray(new String[0]));
        for (String ingredient : config.getOrSet("recipe.data-potion", Arrays.asList("*:264", "%:387"))) {
            try {
                String[] data = ingredient.split(":");
                recipe.setIngredient(
                        data[0].charAt(0),
                        Material.getMaterial(Integer.parseInt(data[1]))
                );
            } catch (Exception e) {
                this.getLogger().severe("Error load recipe '" + ingredient + "', format: 'char:id'");
                e.printStackTrace();
            }
        }
        Bukkit.getServer().addRecipe(recipe);
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
        Bukkit.getPluginManager().registerEvents(new ListenerFrame(this), this);
        Bukkit.getPluginManager().registerEvents(new ListenerPlayer(this), this);
        Bukkit.getPluginManager().registerEvents(new ListenerBlock(this), this);
        Bukkit.getPluginManager().registerEvents(new ListenerPotion(this), this);

    }

    private void loadMessage() {
        for (Message message : Message.values()) {
            message.reload(config);
        }
    }

    @Override
    public void onDisable() {

    }

    public void teleport(Player player, String page1, Teleporter teleporter) {
        if (!player.hasPermission("Transportsscrolls.use")) {
            Message.no_perm.send(player);
        } else {
            PlayerData playerData = PlayerData.datas.get(player);
            TeleportRunnable teleport = playerData.teleport;
            if (teleport != null) {
                Message.wail_old_teleport.send(player);
            } else {
                BookData bookData = this.getBookData(page1.substring(this.codeTeleportBook.length()));
                int amount = teleporter.getMoney();
                if (amount < bookData.getCost()) {
                    Message.missing_items.send(player, "%count%", String.valueOf(bookData.getCost() - amount));
                } else {
                    int cooldown = bookData.calculateCooldown();
                    if (cooldown > 0) {
                        Message.cooldown.send(player, "%sec%", String.valueOf(cooldown));
                    } else {
                        bookData.teleport(playerData, teleporter);
                    }
                }
            }
        }
    }

    public Location fixTeleportLocation(Location loc) {
        Block iterator = loc.getBlock();
        while(iterator.getY() > 0) {
            if (this.isSafeLocation(iterator)) {
                loc = loc.clone();
                loc.setY(iterator.getY() + 1D);
                Block block = loc.getBlock();
                //проверяем блоки со всех сторон
                if (!this.isCollisionBorder(block) || !this.isCollisionBorder(block.getRelative(BlockFace.UP))) {
                    loc.setX(loc.getBlockX() + 0.5D);
                    loc.setZ(loc.getBlockZ() + 0.5D);
                }
                return loc;
            }

            iterator = iterator.getRelative(BlockFace.DOWN);
        }
        return null;
    }

    public boolean isSafeLocation(Block block) {
        if (this.isSafeBlock(block.getTypeId())) {
            Block up = block.getRelative(BlockFace.UP);
            if (notCollision.contains(up.getTypeId())
                    && notCollision.contains(up.getRelative(BlockFace.UP).getTypeId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isSafeBlock(int id) {
        return allowStand.contains(id);
    }

    private List<BlockFace> borders = Arrays.asList( //относительные блоки, в которых можно стоять (после телепортации)
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
            BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST,
            BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST
    );

    private boolean isCollisionBorder(Block block) {
        for (BlockFace face : this.borders) {
            if (!notCollision.contains(block.getRelative(face).getTypeId())) {
                return false;
            }
        }
        return true;
    }
}
