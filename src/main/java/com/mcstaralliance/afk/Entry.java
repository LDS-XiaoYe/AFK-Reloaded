package com.mcstaralliance.afk;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.mcstaralliance.afk.command.AFKCommand;
import com.mcstaralliance.afk.hook.Residence;
import com.mcstaralliance.afk.hook.residence.NewResidence;
import com.mcstaralliance.afk.hook.residence.OldResidence;
import com.mcstaralliance.afk.listener.PlayerCommandPreprocessListener;
import com.mcstaralliance.afk.listener.PlayerDeathListener;
import com.mcstaralliance.afk.listener.PlayerInteractListener;
import com.mcstaralliance.afk.listener.PlayerQuitListener;
import com.mcstaralliance.afk.task.CheckRunnable;
import com.mcstaralliance.afk.task.TeleportRunnable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Entry extends JavaPlugin {

    private static Entry instance;
    private ItemStack item;
    private BukkitTask rewardTask;
    private BukkitTask teleportTask;
    private Residence residenceInstance;

    public static Economy economy = null;

    /**
     * 初始化Vault
     * @return 是否成功
     */
    private boolean initVault(){
        boolean hasNull = false;
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            if ((economy = economyProvider.getProvider()) == null) hasNull = true;
        }
        return !hasNull;
    }
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        instance.saveResource("data.yml",false);
        if (!initVault()){
            getLogger().warning("Vault初始化失败，插件已关闭");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        hookResidence();
        register();
    }

    public void onDisable() {
        rewardTask.cancel();
        teleportTask.cancel();
        item = null;

        // 1.5 Fix: 防止在 /reload 时, 玩家还在AFK时, 全息还在的问题
        AFKCommand.afkPlayer.clear();
        AFKCommand.map.values().forEach(Hologram::delete);
        AFKCommand.map.clear();
    }

    public static Entry getInstance() {
        return instance;
    }

    public Residence getResidenceInstance() {
        return residenceInstance;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getHologramItem() {
        if (item == null) {
            item = new ItemStack(Entry.getInstance().getConfig().getInt("Item.Material"), 1, (short) Entry.getInstance().getConfig().getInt("Item.Data"));
        }
        return item;
    }

    public void resetHoloItem() {
        item = null;
    }

    public void resetTasks() {
        rewardTask.cancel();
        teleportTask.cancel();

        CheckRunnable checkRunnable = new CheckRunnable();
        TeleportRunnable teleportRunnable = new TeleportRunnable();

        rewardTask = Bukkit.getScheduler().runTaskTimer(this, checkRunnable, 30L, Entry.getInstance().getConfig().getLong("Task.RewardPeriod") * 20);
        teleportTask = Bukkit.getScheduler().runTaskTimer(this, teleportRunnable, 30L, Entry.getInstance().getConfig().getLong("Task.TeleportPeriod") * 20);
    }

    private void register() {
        // 命令
        Bukkit.getPluginCommand("gj").setExecutor(new AFKCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessListener(), this);
        CheckRunnable checkRunnable = new CheckRunnable();
        TeleportRunnable teleportRunnable = new TeleportRunnable();

        rewardTask = Bukkit.getScheduler().runTaskTimer(this, checkRunnable, 30L, Entry.getInstance().getConfig().getLong("Task.RewardPeriod") * 20);
        teleportTask = Bukkit.getScheduler().runTaskTimer(this, teleportRunnable, 30L, Entry.getInstance().getConfig().getLong("Task.TeleportPeriod") * 20);
    }

    /**
     * 反射挂钩Residence
     * 1.5.1 Fix: 当服务器未安装Residence时所出现的NPE
     */
    private void hookResidence() {
        if (Bukkit.getPluginManager().getPlugin("Residence") != null) {
            String residenceVersion = Bukkit.getPluginManager().getPlugin("Residence").getDescription().getVersion();
            residenceInstance = new OldResidence();
            if (residenceVersion.startsWith("4")) {
                residenceInstance = (Residence) new NewResidence();
            }
        }
    }
}
