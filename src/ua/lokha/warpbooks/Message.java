package ua.lokha.warpbooks;

import org.bukkit.command.CommandSender;

public enum Message {

    crafted_book("&aTeleport-book\n" +
            "&7Click in hand to\n" +
            "&7put the point\n" +
            "&7of teleportation."),
    teleported_book_name("&aTeleport by %x%,%y%,%z%"),
    teleported_book_lore("&7Click to teleport.\n" +
            " \n" +
            "&eDura: &c%count%\n" +
            "&ePrice: &c%cost% emeralds\n" +
            "&eCooldown: &c%cooldown% sec\n" +
            "&eWarmup: &c%warmup% sec"),
    teleported_potion("%name%\n" +
            "&7Throw to teleport.\n" +
            " \n" +
            "&ePrice: &c%cost% emeralds"),
    teleport_later("&eTeleportation occurs in %second% seconds."),
    teleported("&aTeleportation... left %count% uses."),
    teleported_last("&aTeleportation... &cit was the last use."),
    teleported_unlimited("&aTeleportation..."),
    teleported_book_created("&aThe book created a teleportation!"),
    crafted_book_crafted("&aYou have created a book teleport! Click it in any place to save location."),
    crafted_potion_crafted("&aYou have created a potion teleport! Throw to teleport."),
    cancel_telepor_move("&cTeleporting canceled, do not move."),
    wail_old_teleport("&cWait previous teleport."),
    no_perm("&cYou are not allowed."),
    missing_items("&cYou are missing %count% emeralds."),
    teleporter_not_fount("&cTeleportation abolished, take a book in inventory."),
    command_desc("&e==========[Teleportator]==========\n" +
            "&4/tpbookedit cost [value] &7- set cost\n" +
            "&4/tpbookedit durability [value] &7- set durability\n" +
            "&4/tpbookedit cooldown [value] &7- set cooldown\n" +
            "&4/tpbookedit warmup [value] &7- set warmup"),
    argument_not_found("&cArgument not found."),
    take_book_in_hand("&cTake the book-teleporter in hand."),
    write_value("&cWrite value."),
    bad_value("&cBad value '%value%'."),
    value_changed("&aValue is changed."),
    bad_location("&cUnsafe point teleporting."),
    cooldown("&cYou can use the teleporter in %sec% seconds."),
    sponge_deny_teleport("&cSponge blocks teleportation.")
    ,;

    private String def;
    private String value;

    Message(String def) {
        this.value = this.def = def;
    }

    public void reload(Config config) {
        this.value = config.getOrSet("message." + this.name().replace("_", "-"), this.def).replace("&", "§");
    }

    public void send(CommandSender sender) {
        if (!this.value.isEmpty()) {
            sender.sendMessage(this.value);
        }
    }

    public void send(CommandSender sender, String... replaced) {
        String mess = this.get(replaced);
        if (!mess.isEmpty()) {
            sender.sendMessage(mess);
        }
    }

    public String getValue() {
        return value;
    }

    public String get(String... replaced) {
        String message = this.value;
        for (int i = 0; i < replaced.length; i += 2) {
            message = message.replace(replaced[i], replaced[i + 1]);
        }
        return message;
    }
}
