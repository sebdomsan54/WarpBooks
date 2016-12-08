package ua.lokha.warpbooks;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Command implements CommandExecutor, TabExecutor {

    private WarpBooksPlugin plugin;

    public Command(WarpBooksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can not write to the console.");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("Transportsscrolls.command")) {
            Message.no_perm.send(player);
            return false;
        }
        if (args.length == 0) {
            Message.command_desc.send(player);
            return true;
        }

        args[0] = args[0].toLowerCase();

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) {
            Message.take_book_in_hand.send(player);
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (!(itemMeta instanceof BookMeta)) {
            Message.take_book_in_hand.send(player);
            return false;
        }

        BookMeta bookMeta = (BookMeta) itemMeta;
        if (bookMeta.getPageCount() <= 0) {
            Message.take_book_in_hand.send(player);
            return false;
        }

        String page1 = bookMeta.getPage(1);
        if (!page1.startsWith(plugin.codeTeleportBook)) {
            Message.take_book_in_hand.send(player);
            return false;
        }

        BookData bookData = plugin.getBookData(page1.substring(plugin.codeTeleportBook.length()));

        if (args.length < 2) {
            Message.write_value.send(player);
            return false;
        }

        if (!args0.contains(args[0])) {
            Message.argument_not_found.send(player);
            return false;
        }

        Integer value;
        try {
            value = Integer.parseInt(args[1]);
        } catch (Exception e) {
            Message.bad_value.send(player, "%value%", args[1]);
            return false;
        }

        if (value < -1) {
            value = -1;
        }

        if (args[0].equals("cost")) {
            bookData.setCost(value);
            bookData.setData(item);
            player.getInventory().setItemInMainHand(item);
            Message.value_changed.send(player);
            return true;
        }

        if (args[0].equals("durability")) {
            bookData.setDuramility(value);
            bookData.setData(item);
            player.getInventory().setItemInMainHand(item);
            Message.value_changed.send(player);
            return true;
        }

        if (args[0].equals("warmup")) {
            bookData.setWarmup(value);
            bookData.setData(item);
            player.getInventory().setItemInMainHand(item);
            Message.value_changed.send(player);
            return true;
        }

        if (args[0].equals("cooldown")) {
            bookData.setCooldown(value);
            bookData.setData(item);
            player.getInventory().setItemInMainHand(item);
            Message.value_changed.send(player);
            return true;
        }

        Message.argument_not_found.send(player);
        return false;
    }

    private List<String> args0 = Arrays.asList("cost", "durability", "warmup", "cooldown");

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (!sender.hasPermission("Transportsscrolls.command")) {
            return null;
        }
        if (args.length == 1) {
            return args0.stream().filter(arg->StringUtils.containsIgnoreCase(arg, args[0])).collect(Collectors.toList());
        }

        if (args.length == 2) {
            return Arrays.asList("-1", "10", "20").stream().filter(arg->StringUtils.containsIgnoreCase(arg, args[1])).collect(Collectors.toList());
        }
        return null;
    }
}
