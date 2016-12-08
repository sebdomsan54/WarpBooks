package ua.lokha.warpbooks;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    public static Map<Player, PlayerData> datas = new HashMap<>();

    public Player player;
    public TeleportRunnable teleport;
    public long lastPhisic = System.currentTimeMillis();

    public PlayerData(Player player) {
        this.player = player;
    }

    public boolean hasLastPhisic() {
        boolean result = System.currentTimeMillis() - this.lastPhisic > 1000;
        if (result) {
            this.lastPhisic = System.currentTimeMillis();
        }
        return result;
    }
}
