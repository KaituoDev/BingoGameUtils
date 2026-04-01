package fun.kaituo.bingoGameUtils;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import fun.kaituo.bingoGameUtils.commands.TeamTeleportCommand;
import fun.kaituo.bingoGameUtils.voicechat.VoicechatManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BingoGameUtils extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Enable VoiceChat Manager
        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service == null) {
            getLogger().warning("BingoGameUtils failed to get bukkit voice chat service!");
            return;
        }

        VoicechatManagerPlugin voiceChatPlugin = new VoicechatManagerPlugin(this);
        service.registerPlugin(voiceChatPlugin);
        voiceChatPlugin.createSignalListener();
        getLogger().info("BingoGameUtils VoicechatManager enabled.");

        // Enable /team_teleport Command
        getCommand("team_teleport").setExecutor(new TeamTeleportCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getScheduler().cancelTasks(this);
    }
}
