package com.mcstaralliance.afk.util;

import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.mcstaralliance.afk.Entry;
import org.bukkit.entity.Player;

/**
 * 领地工具类
 *
 * @author May_Speed
 */
public class ResidenceUtils {

    /**
     * 判断一名玩家是否在领地内
     *
     * @param player 玩家
     * @return true[是]/false[不是]
     */
    public static boolean isInResidence(Player player) {
        ResidenceManager residenceManager = Entry.getInstance().getResidenceInstance().getResidenceManager();
        return residenceManager.getByLoc(player.getLocation()) != null;
    }
}
