package com.mcstaralliance.afk.listener;

import com.mcstaralliance.afk.command.AFKCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * 玩家死亡监听
 * <p>
 * 本类作用: 当玩家还在挂机时突然死亡的情况
 *
 * @author May_Speed
 */
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player player = e.getEntity();
        if (AFKCommand.afkPlayer.contains(player.getName())) {
            AFKCommand.map.get(player.getName()).delete();
            AFKCommand.afkPlayer.remove(player.getName());
        }
    }
}
