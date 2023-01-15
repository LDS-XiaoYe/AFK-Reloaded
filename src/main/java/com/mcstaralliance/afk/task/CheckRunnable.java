package com.mcstaralliance.afk.task;

import com.mcstaralliance.afk.Entry;
import com.mcstaralliance.afk.command.AFKCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import static com.mcstaralliance.afk.Entry.economy;
import static com.mcstaralliance.afk.command.AFKCommand.saveFiles;

public class CheckRunnable implements Runnable {

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        LocalTime now = LocalTime.now();
        File config = new File(com.mcstaralliance.afk.Entry.getPlugin(com.mcstaralliance.afk.Entry.class).getDataFolder(),"data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(config);
        if(now.getHour() == 5 && now.getMinute() == 0){
            config.delete();
        }
        // 不太优雅，在这一分钟会删好多次
        for (int i = 0; i < AFKCommand.afkPlayer.size(); i++) {
            String name = AFKCommand.afkPlayer.get(i);
            if (AFKCommand.map.get(name) == null) {
                AFKCommand.afkPlayer.remove(name);
                continue;
            }
            /* 离线检查开始 */
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
            /* 离线检查结束 */

            Player player = offlinePlayer.getPlayer();
            data.set(player.getName(), data.getDouble(player.getName(), 0.0) + Entry.getInstance().getConfig().getDouble("Task.Money"));
            saveFiles(data,config);
            if(data.getDouble(player.getName(), 0.0) >= Entry.getInstance().getConfig().getInt("Task.MaxDailyMoney")){
                player.sendMessage(Entry.getInstance().getConfig().getString("Task.ReachMaxMessage").replaceAll("&", "§"));
            }else{
                List<String> commands = Entry.getInstance().getConfig().getStringList("Task.Commands");
                commands.forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("&", "§").replaceAll("%player%", player.getName())));
            }


//            player.giveExp(Entry.getInstance().getConfig().getInt("Task.Exp"));
//            Entry.getInstance().getEconomy().addMoney(name, Entry.getInstance().getConfig().getDouble("Task.Money"));
        }
    }
}
