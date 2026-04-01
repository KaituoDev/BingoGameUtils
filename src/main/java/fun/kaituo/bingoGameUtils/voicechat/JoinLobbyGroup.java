package fun.kaituo.bingoGameUtils.voicechat;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinLobbyGroup {

    public static final String LOBBY_JOIN_MESSAGE = "§f已将你加入大厅语音聊天频道！";

    private static final String LOBBY_GROUP_NAME = "Lobby";
    public static final UUID LOBBY_GROUP_UUID = UUID.fromString("0372627d-c783-4844-b06b-e51f891ebf26");

    public static void initializeGroups(VoicechatServerApi api) {
        // Remove all groups
        List<UUID> groupIds = new ArrayList<>();
        for (Group group : api.getGroups()) {
            groupIds.add(group.getId());
        }
        for (UUID groupId : groupIds) {
            api.removeGroup(groupId);
        }

        // Create lobby and spectator group
        api.groupBuilder()
                .setId(LOBBY_GROUP_UUID)
                .setName(LOBBY_GROUP_NAME)
                .setPersistent(true)
                .setType(Group.Type.ISOLATED)
                .build();
    }

    public static void allJoinLobby(VoicechatServerApi api) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            VoicechatConnection connection = api.getConnectionOf(player.getUniqueId());
            if (connection == null) {
                continue;
            }

            connection.setGroup(api.getGroup(LOBBY_GROUP_UUID));
            player.sendMessage(LOBBY_JOIN_MESSAGE);
        }
    }
}
