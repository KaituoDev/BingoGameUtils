package fun.kaituo.bingoGameUtils.voicechat;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import fun.kaituo.bingoGameUtils.BingoGameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
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
        VoicechatConnection connection = pce.getConnection();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (connection == null) {
                return;
            }

            Player player = (Player) connection.getPlayer();
            JoinTeamGroups.joinTeamGroup(player, connection, api);
        }, JOIN_GROUPS_DELAY);

    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(PlayerConnectedEvent.class, this::onPlayerConnected);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    public void createSignalListener() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            World world = Bukkit.getServer().getWorlds().getFirst();

            if (world.getBlockAt(187, 140, 79).getType().equals(Material.LIME_STAINED_GLASS)) {
                JoinLobbyGroup.initializeGroups(api);
                JoinLobbyGroup.allJoinLobby(api);
                world.getBlockAt(187, 140, 79).setType(Material.AIR);

            } else if (world.getBlockAt(187, 140, 78).getType().equals(Material.RED_STAINED_GLASS)) {
                JoinTeamGroups.initializeTeamGroups(api);
                JoinTeamGroups.formTeamGroups(api);
                world.getBlockAt(187, 140, 78).setType(Material.AIR);
            }
        }, 20, 1);
    }
}
