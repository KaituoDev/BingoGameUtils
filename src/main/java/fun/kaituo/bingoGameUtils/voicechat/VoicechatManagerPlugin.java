package fun.kaituo.bingoGameUtils.voicechat;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import fun.kaituo.bingoGameUtils.BingoGameUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VoicechatManagerPlugin implements VoicechatPlugin {

    private final BingoGameUtils plugin;
    private static VoicechatServerApi api;

    public VoicechatManagerPlugin(BingoGameUtils plugin) {
        this.plugin = plugin;
    }

    private String PLUGIN_ID = "bingogameutils";

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    public static final int JOIN_GROUPS_DELAY = 20; // In server ticks

    public void onServerStarted(VoicechatServerStartedEvent vsse) {
        api = vsse.getVoicechat();
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> JoinLobbyGroup.initializeGroups(api),
                JOIN_GROUPS_DELAY);
    }

    public void onPlayerConnected(PlayerConnectedEvent pce) {
        Player player = (Player) pce.getConnection().getPlayer().getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player == null) {
                return;
            }

            if (!JoinTeamGroups.joinGroup(player, api.getConnectionOf(player.getUniqueId()), api)) {
                JoinLobbyGroup.joinGroup(player, api);
            }
        }, JOIN_GROUPS_DELAY);

    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(PlayerConnectedEvent.class, this::onPlayerConnected);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    public static void onGameStart() {
        JoinTeamGroups.initializeTeamGroups(api);
        JoinTeamGroups.formTeamGroups(api);
    }

    public static void onGameEnd() {
        JoinLobbyGroup.initializeGroups(api);
        JoinLobbyGroup.allJoinLobby(api);
    }
}
