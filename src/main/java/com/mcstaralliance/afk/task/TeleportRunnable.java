package com.mcstaralliance.afk.task;
import com.mcstaralliance.afk.command.AFKCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * 本类用于防止玩家走动
 *
 * @author Zoyn
 * @since 2018-03-17
 */
public class TeleportRunnable implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < AFKCommand.afkPlayer.size(); i++) {
            String name = AFKCommand.afkPlayer.get(i);
            if (AFKCommand.map.get(name) == null) {
                AFKCommand.afkPlayer.remove(name);
                continue;
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (offlinePlayer == null) {
                AFKCommand.afkPlayer.remove(name);
                AFKCommand.map.get(name).delete();
                continue;
            }
            if (!offlinePlayer.isOnline()) {
                AFKCommand.afkPlayer.remove(name);
                AFKCommand.map.get(name).delete();
                continue;
            }
            Player player = offlinePlayer.getPlayer();
            Location loc = AFKCommand.map.get(name).getLocation();
            player.teleport(loc.clone().add(0D, -3.25D, 0D)); // TP 回原位置
        }
    }
}
