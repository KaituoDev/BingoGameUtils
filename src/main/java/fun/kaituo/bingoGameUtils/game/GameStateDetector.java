package fun.kaituo.bingoGameUtils.game;

import fun.kaituo.bingoGameUtils.BingoGameUtils;
import fun.kaituo.bingoGameUtils.voicechat.VoicechatManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

public class GameStateDetector {

    public static void registerDetector(BingoGameUtils plugin) {
        World world = Bukkit.getServer().getWorlds().getFirst();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (world.getBlockAt(187, 140, 79).getType().equals(Material.LIME_STAINED_GLASS)) {
                BingoGameUtils.isGameRunning = false;
                GameStateDetector.onGameEnd();
                world.getBlockAt(187, 140, 79).setType(Material.AIR);

            } else if (world.getBlockAt(187, 140, 78).getType().equals(Material.RED_STAINED_GLASS)) {
                BingoGameUtils.isGameRunning = true;
                GameStateDetector.onGameStart();
                world.getBlockAt(187, 140, 78).setType(Material.AIR);
            }
        }, 10, 1);
    }

    private static void onGameStart() {
        VoicechatManagerPlugin.onGameStart();
    }

    private static void onGameEnd() {
        VoicechatManagerPlugin.onGameEnd();
    }
}
