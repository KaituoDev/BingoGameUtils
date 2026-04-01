package fun.kaituo.bingoGameUtils.voicechat;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

import static fun.kaituo.bingoGameUtils.voicechat.JoinLobbyGroup.LOBBY_GROUP_UUID;

public class JoinTeamGroups {

    private static final String TEAM_JOIN_MESSAGE = "§f已将你加入你所在队伍的语音聊天频道！";
    private static final String TEAM_JOIN_FAIL_WARNING = "§c加入队伍语音聊天频道失败！";

    private static final String RED_GROUP_NAME = "Red";
    public static final UUID RED_GROUP_UUID = UUID.fromString("15024664-950f-40e3-a28f-032badf17232");
    private static final String YELLOW_GROUP_NAME = "Yellow";
    public static final UUID YELLOW_GROUP_UUID = UUID.fromString("220e5de3-ac56-4310-97e9-0dbc711da911");
    private static final String BLUE_GROUP_NAME = "Blue";
    public static final UUID BLUE_GROUP_UUID = UUID.fromString("33ff96d4-0523-449c-94f9-cbb142dc6640");
    private static final String GREEN_GROUP_NAME = "Green";
    public static final UUID GREEN_GROUP_UUID = UUID.fromString("4175c107-959d-4798-873c-32ea1dd40435");

    public static void initializeTeamGroups(VoicechatServerApi api) {
        JoinLobbyGroup.initializeGroups(api);

        api.groupBuilder()
                .setId(RED_GROUP_UUID)
                .setName(RED_GROUP_NAME)
                .setPersistent(true)
                .setType(Group.Type.ISOLATED)
                .build();
        api.groupBuilder()
                .setId(YELLOW_GROUP_UUID)
                .setName(YELLOW_GROUP_NAME)
                .setPersistent(true)
                .setType(Group.Type.ISOLATED)
                .build();
        api.groupBuilder()
                .setId(BLUE_GROUP_UUID)
                .setName(BLUE_GROUP_NAME)
                .setPersistent(true)
                .setType(Group.Type.ISOLATED)
                .build();
        api.groupBuilder()
                .setId(GREEN_GROUP_UUID)
                .setName(GREEN_GROUP_NAME)
                .setPersistent(true)
                .setType(Group.Type.ISOLATED)
                .build();
    }

    public static void formTeamGroups(VoicechatServerApi api) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            VoicechatConnection connection = api.getConnectionOf(player.getUniqueId());
            if (connection == null) {
                continue;
            }

            joinTeamGroup(player, connection, api);
        }
    }

    public static Boolean joinTeamGroup(Player player, VoicechatConnection connection, VoicechatServerApi api) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(player);
        if (team == null) {
            return false;
        }
        if (!team.hasColor()) {
            return false;
        }

        switch (team.getColor()) {
            case RED :
                connection.setGroup(api.getGroup(RED_GROUP_UUID));
                player.sendMessage(TEAM_JOIN_MESSAGE);
                return true;
            case GOLD :
                connection.setGroup(api.getGroup(YELLOW_GROUP_UUID));
                player.sendMessage(TEAM_JOIN_MESSAGE);
                return true;
            case DARK_AQUA :
                connection.setGroup(api.getGroup(BLUE_GROUP_UUID));
                player.sendMessage(TEAM_JOIN_MESSAGE);
                return true;
            case GREEN :
                connection.setGroup(api.getGroup(GREEN_GROUP_UUID));
                player.sendMessage(TEAM_JOIN_MESSAGE);
                return true;
        }

        player.sendMessage(TEAM_JOIN_FAIL_WARNING);
        connection.setGroup(api.getGroup(LOBBY_GROUP_UUID));
        return false;
    }
}
