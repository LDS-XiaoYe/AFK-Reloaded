package com.mcstaralliance.afk.hook.residence;

import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.mcstaralliance.afk.hook.Residence;

/**
 * @author LDS_XiaoYe
 */
public class NewResidence implements Residence {

    @Override
    public ResidenceManager getResidenceManager() {
        return com.bekvon.bukkit.residence.Residence.getInstance().getResidenceManager();
    }
}
