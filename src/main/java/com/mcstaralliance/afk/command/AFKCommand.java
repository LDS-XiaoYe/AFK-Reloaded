package com.mcstaralliance.afk.command;

import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mcstaralliance.afk.Entry;
import com.mcstaralliance.afk.util.ResidenceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.mcstaralliance.afk.Entry.economy;

public class AFKCommand implements CommandExecutor {

    public static List<String> afkPlayer = Lists.newArrayList();
    public static HashMap<String, Hologram> map = Maps.newHashMap();

    public static void saveFiles(FileConfiguration files,File config){
        try {
            files.save(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    File config = new File(com.mcstaralliance.afk.Entry.getPlugin(com.mcstaralliance.afk.Entry.class).getDataFolder(),"data.yml");
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("gj")) {
            if (args.length == 0) {
                sender.sendMessage("§7====== §8[§6AFK§8] §7======");
                sender.sendMessage("§b/gj on §7开启挂机状态");
                sender.sendMessage("§b/gj off §7关闭挂机状态");
                sender.sendMessage("§b/gj reload §7重载插件");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("§f请在游戏中输入!");
                return true;
            }

            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("gj.reload")) {
                    player.sendMessage("§c权限不足!");
                    return true;
                }
                player.sendMessage("§8[§6挂机§8] §e> §a插件已重载");
                Entry.getInstance().reloadConfig();
                // 1.5 Fix: 修复 reload 时物品尚未重置的问题, 和任务尚未重置的问题
                Entry.getInstance().resetHoloItem();
                Entry.getInstance().resetTasks();

                return true;
            }
            if (args[0].equalsIgnoreCase("clear")) {
                if (!player.hasPermission("gj.clear")) {
                    player.sendMessage("§c权限不足!");
                    return true;
                }
                config.delete();
                player.sendMessage("§8[§6挂机§8] §e> §a数据已清除");
                return true;
            }
            if (args[0].equalsIgnoreCase("test")) {
                if (!player.hasPermission("gj.test")) {
                    player.sendMessage("§c权限不足!");
                    return true;
                }
                FileConfiguration myConfig = YamlConfiguration.loadConfiguration(config);
                myConfig.set(player.getName(), economy.getBalance(player));
                try {
                    myConfig.save(config);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("test2")) {
                if (!player.hasPermission("gj.test2")) {
                    player.sendMessage("§c权限不足!");
                    return true;
                }
                FileConfiguration myConfig = YamlConfiguration.loadConfiguration(config);
                myConfig.set(player.getName(), 0);
                try {
                    myConfig.save(config);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            if ((args[0].equalsIgnoreCase("on"))) {
                if (!player.hasPermission("gj.on")) {
                    player.sendMessage("§c权限不足!");
                    return true;
                }
                if (afkPlayer.contains(player.getName())) {
                    // 如果玩家处在挂机列表中
                    player.sendMessage(Entry.getInstance().getConfig().getString("Tips.AFKIng").replaceAll("&", "§"));
                    return true;
                }
                // 1.4 Fix: 修复玩家在空中时做挂机的操作
                if (player.getLocation().add(0D, -1D, 0D).getBlock().getType() == Material.AIR) {
                    player.sendMessage(Entry.getInstance().getConfig().getString("Tips.WhenInAirAFK").replaceAll("&", "§"));
                    return true;
                }

                Location loc = player.getLocation();
                // 1.4 Feature: 区域挂机
                if (Entry.getInstance().getConfig().getBoolean("Residence.Enable")) {
                    if (!ResidenceUtils.isInResidence(player)) {
                        player.sendMessage(Entry.getInstance().getConfig().getString("Residence.NoInAResidence").replaceAll("&", "§"));
                        return true;
                    }

                    ResidenceManager residenceManager = Entry.getInstance().getResidenceInstance().getResidenceManager();
                    String residenceName = Entry.getInstance().getConfig().getString("Residence.Name");
                    if (!residenceManager.getByLoc(loc).getName().equals(residenceName)) {
                        player.sendMessage(Entry.getInstance().getConfig().getString("Residence.NoInRightResidence")
                                .replaceAll("&", "§")
                                .replaceAll("%res_name%", residenceName)
                        );
                        return true;
                    }
                }

                // hologram 位置设置 + 生成部分
                loc.setY(loc.getY() + 3.25);
                // 位置向上提升 3.25 格
                Hologram hologram = HologramsAPI.createHologram(Entry.getInstance(), loc);
                hologram.appendItemLine(Entry.getInstance().getHologramItem());
                hologram.appendTextLine(Entry.getInstance().getConfig().getString("Item.Line").replaceAll("&", "§"));

                afkPlayer.add(player.getName());
                // 将玩家添加进挂机者列表中
                map.put(player.getName(), hologram);

                // 信息提示
                player.sendMessage(Entry.getInstance().getConfig().getString("Tips.AFKRun").replaceAll("&", "§"));
                return true;
            }

            if ((args[0].equalsIgnoreCase("off"))) {
                if (!afkPlayer.contains(player.getName())) {
                    player.sendMessage(Entry.getInstance().getConfig().getString("Tips.IsNotAFK").replaceAll("&", "§"));
                    return true;
                }
                // hologram 删除 + 移除挂机列表中的该玩家部分
                for (int i = 0; i < afkPlayer.size(); i++) {
                    if (afkPlayer.get(i).contains(player.getName())) {
                        map.get(player.getName()).delete();
                        afkPlayer.remove(player.getName());
                        player.sendMessage(Entry.getInstance().getConfig().getString("Tips.AFKQuit").replaceAll("&", "§"));
                    }
                }
                return true;
            }
        }
        return false;
    }
}
