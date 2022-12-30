package com.mcstaralliance.afk.listener;

import com.mcstaralliance.afk.command.AFKCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 玩家退出服务器监听
 * <p>
 * 本类作用: 当玩家还在挂机时 突然掉线 和 突然断开 连接之类的情况
 *
 * @author May_Speed
 */
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (AFKCommand.afkPlayer.contains(player.getName())) {
            AFKCommand.map.get(player.getName()).delete();
            AFKCommand.afkPlayer.remove(player.getName());
        }
    }
}
