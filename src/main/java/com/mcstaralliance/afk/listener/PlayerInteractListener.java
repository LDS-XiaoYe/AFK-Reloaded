package com.mcstaralliance.afk.listener;

import com.mcstaralliance.afk.Entry;
import com.mcstaralliance.afk.command.AFKCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * 玩家死亡监听
 * <p>
 * 本类作用: 当玩家还在挂机时使用一些物品的情况
 *
 * @author May_Speed
 */
public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (AFKCommand.afkPlayer.contains(player.getName())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Entry.getInstance().getConfig().getString("Tips.AFKUseCommand").replaceAll("&", "§"));
        }
    }

}
