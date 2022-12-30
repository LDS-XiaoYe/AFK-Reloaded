package com.mcstaralliance.afk.listener;

import com.mcstaralliance.afk.Entry;
import com.mcstaralliance.afk.command.AFKCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * 玩家使用命令监听
 * 本类作用: 当玩家还在挂机时使用命令的情况
 *
 * @author May_Speed
 */
public class PlayerCommandPreprocessListener implements Listener {

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (AFKCommand.afkPlayer.contains(player.getName())) {
            String msg = e.getMessage().toLowerCase();
            if ((!msg.equals("/gj")) && (!msg.startsWith("/gj "))) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Entry.getInstance().getConfig().getString("Tips.AFKUseCommand").replaceAll("&", "§"));
            }
        }
    }

}
