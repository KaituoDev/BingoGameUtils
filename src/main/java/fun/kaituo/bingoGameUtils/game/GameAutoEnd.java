package fun.kaituo.bingoGameUtils.game;

import fun.kaituo.bingoGameUtils.BingoGameUtils;
import org.bukkit.Bukkit;

public class GameAutoEnd {

    private static int gameAutoEndCountdown = 0;

    public static void registerAutoEndCountdown(BingoGameUtils plugin) {

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!BingoGameUtils.isGameRunning) {
                gameAutoEndCountdown = 0;
                return;
            }

            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                gameAutoEndCountdown = 60;
                return;
            }
            else if (gameAutoEndCountdown == 60) {
                plugin.getLogger().info("Detected no players in the server, try stop game in 1 minute.");
            }

            if (gameAutoEndCountdown > 0) {
                --gameAutoEndCountdown;

                if (gameAutoEndCountdown == 0) {
                    plugin.getLogger().info("Detected no players in the server, try stop BINGO game.");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "function flytre:win/all");
                }
            }
        }, 5, 20);
    }
}
