package gym.core.runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import gym.core.Core;
import gym.core.profile.Profile;

public class CheckRunnable extends BukkitRunnable {
    
    private Core main;
    private int maxCps;
    private String alertMessage;
    
    public CheckRunnable(final Core main, int maxCps, String message){
        this.main = main;
        this.maxCps = maxCps;
        this.alertMessage = message;
    }
    
    @Override
    public void run() {
        for (Profile wp : this.main.getManagerHandler().getProfileManager().getProfiles().values()){
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player != null) {
                int ping = player.getPing();
                double tps = Math.round(Bukkit.spigot().getTPS()[0] * 1e2) / 1e2;
                int antiLag = (int)((20.0D - tps) * 2.0D);
                antiLag += ping / 50;
                if ((wp.clicks[0] >= maxCps + antiLag) && (wp.lastAlert + 2 * 1000L < System.currentTimeMillis())) {
                    wp.lastAlert = System.currentTimeMillis();
                    for (Player toAlert : Bukkit.getOnlinePlayers()){
                        if (toAlert.hasPermission(this.main.getLoaderHandler().getPermission().getViewCps())){
                            toAlert.sendMessage(alertMessage.replace("%player%", player.getName()).replace("%cps%", String.valueOf(wp.clicks[0])).replace("%ms%", String.valueOf(ping))
                                    .replace("%tps%", String.valueOf(tps))
                                    .replace("&", "§"));
                        }
                        wp.autoclickAlert += 1; 
                    }
                }
                System.arraycopy(wp.clicks, 0, wp.clicks, 1, wp.clicks.length - 1);
                wp.clicks[0] = 0;
            }
        }
    }
}



