package com.mcstaralliance.afk.hook.residence;

import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.mcstaralliance.afk.hook.Residence;

import java.lang.reflect.Method;

public class OldResidence implements Residence {

    private ResidenceManager objectManager = null;

    @Override
    public ResidenceManager getResidenceManager() {
        if (objectManager != null) {
            return objectManager;
        }
        try {
            Method method = com.bekvon.bukkit.residence.Residence.class.getMethod("getResidenceManager");
            Object object = method.invoke(null);
            objectManager = (ResidenceManager) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectManager;
    }

}
