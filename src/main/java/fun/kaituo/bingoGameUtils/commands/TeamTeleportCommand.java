package fun.kaituo.bingoGameUtils.commands;

import fun.kaituo.bingoGameUtils.BingoGameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TeamTeleportCommand implements CommandExecutor, TabCompleter {
    private final String WRONG_COMMAND_USAGE = "§c正确用法：直接输入/team_teleport 或 /ttp";

    private final int TELEPORT_COOLDOWN = 59; // in server ticks

    private final static HashMap<ChatColor, Set<Player>> teamPlayerMap = new HashMap<>();

    private final static HashMap<Player, Integer> playerTeleportCooldown = new HashMap<>();

    public TeamTeleportCommand(BingoGameUtils plugin) {
        playerTeleportCooldown.clear();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            playerTeleportCooldown.replaceAll((p, i) -> (i - 1));

            for (Player p : playerTeleportCooldown.keySet()) {
                if (playerTeleportCooldown.get(p) <= 0) {
                    playerTeleportCooldown.remove(p);
                }
            }
        }, TELEPORT_COOLDOWN, 1);
    }

    @Override // Return true if a valid command, otherwise false
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!command.getName().equalsIgnoreCase("team_teleport")) {
            return false;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }
        if (strings.length > 0) {
            player.sendMessage(WRONG_COMMAND_USAGE);
            return true;
        }

        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(player);
        if (team == null) {
            player.sendMessage("§c你必须在有效队伍中才能使用此命令！");
            return true;
        }
        if (!team.hasColor()) {
            player.sendMessage("§c你必须在有效队伍中才能使用此命令！");
            return true;
        }

        if (playerTeleportCooldown.containsKey(player)
                && playerTeleportCooldown.get(player) > 0) {
            int remainSeconds = playerTeleportCooldown.get(player)/20 + 1;
            player.sendMessage("§c你需要等待§6" + remainSeconds + "§c秒，才能再次尝试传送至队友！");
            return true;
        }

        refreshTeamPlayerMap();
        ArrayList<Player> teleportablePlayers = new ArrayList<>();
        for (Player p : teamPlayerMap.get(team.getColor())) {
            if (p == null) {
                teamPlayerMap.get(team.getColor()).remove(p);
                continue;
            }
            if (p.getUniqueId() == player.getUniqueId()) {
                continue;
            }
            if (p.isDead()) {
                continue;
            }
            if (p.getLocation().getBlockX() < 10000 && p.getLocation().getBlockX() > -10000
                    && p.getLocation().getBlockZ() < 10000 && p.getLocation().getBlockZ() > -10000) {
                continue;
            }

            teleportablePlayers.add(p);
        }

        if (teleportablePlayers.isEmpty()) {
            player.sendMessage("§c未找到可传送的队友！");
            return true;
        }

        Collections.shuffle(teleportablePlayers);
        player.teleport(teleportablePlayers.getFirst());
        player.sendMessage("§a已将你传送至" + teleportablePlayers.getFirst().getName());
        playerTeleportCooldown.put(player, TELEPORT_COOLDOWN);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, String[] args) {
        return List.of("");
    }

    private static void refreshTeamPlayerMap() {
        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        teamPlayerMap.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = mainScoreboard.getEntityTeam(player);
            if (team == null) {
                continue;
            }
            if (!team.hasColor()) {
                continue;
            }

            ChatColor color = team.getColor();
            if (!teamPlayerMap.containsKey(color)) {
                teamPlayerMap.put(color, new HashSet<>());
            }
            teamPlayerMap.get(color).add(player);
        }
    }
}
