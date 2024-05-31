package gym.core.handler.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import gym.core.Core;
import kezukdev.akyto.duel.Duel;
import kezukdev.akyto.profile.ProfileState;

public class PracticeListener implements Listener {
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Core.API.getPracticeAPI().getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).isInState(ProfileState.MOD)) {
        	if (event.getItem().getType().equals(Material.NETHER_STAR)) {
        		if (Bukkit.getPluginManager().getPlugin("aPractice") != null) {
            		if (Core.API.getPracticeAPI().getDuels().isEmpty()) {
            			event.getPlayer().sendMessage(ChatColor.RED + "0 player is in match!");
            			return;
            		}
            		final List<UUID> playersInMatch = new ArrayList<>();
            		Core.API.getPracticeAPI().getDuels().forEach(duel -> {
            			playersInMatch.addAll(duel.getFirst());
            			playersInMatch.addAll(duel.getSecond());
            		});
            		Collections.shuffle(playersInMatch);
            		event.getPlayer().teleport(Bukkit.getPlayer(playersInMatch.get(0)));
    				final Duel duel = kezukdev.akyto.utils.Utils.getDuelByUUID(playersInMatch.get(0));	
            		List<UUID> duelPlayers = new ArrayList<>();
            		duelPlayers.addAll(duel.getFirst());
            		duelPlayers.addAll(duel.getSecond());
            		duelPlayers.forEach(uuid -> event.getPlayer().showPlayer(Bukkit.getPlayer(uuid)));
            		Core.API.getLoaderHandler().getMessage().getRandomTeleport().forEach(msg -> {
            			event.getPlayer().sendMessage(msg.replace("%target%", Bukkit.getPlayer(playersInMatch.get(0)).getName()).replace("%playerOne%", Bukkit.getPlayer(new ArrayList<>(duel.getFirst()).get(0)).getName()).replace("%playerTwo%", Bukkit.getPlayer(new ArrayList<>(duel.getSecond()).get(0)).getName()).replace("%matchLadder%", ChatColor.stripColor(duel.getKit().displayName())).replace("%matchDuration%", this.getFormattedDuration(duel)));
            		});
        		}
        		return;
        	}
        	if (event.getItem().getType().equals(Material.REDSTONE_TORCH_ON)) {
        		event.setUseInteractedBlock(Result.DENY);
        		event.setUseItemInHand(Result.DENY);
        		event.setCancelled(true);
        		event.getPlayer().chat("/mod");
        		return;
        	}
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
    	if (Core.API.getPracticeAPI().getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).isInState(ProfileState.MOD)) {
            Player clicker = event.getPlayer();
            if (clicker.getInventory().getItemInHand().getType().equals(Material.PACKED_ICE) || clicker.getInventory().getItemInHand().getType().equals(Material.PAPER)  || clicker.getInventory().getItemInHand().getType().equals(Material.SKULL_ITEM)) {
                if (event.getRightClicked() instanceof Player) {
                    Player clicked = (Player) event.getRightClicked();
                    if (clicker.getInventory().getItemInHand().getType().equals(Material.SKULL_ITEM)) {
                    	clicker.chat("/stats " + clicked.getName());
                    	return;
                    }
                    if (clicker.getInventory().getItemInHand().getType().equals(Material.PAPER)) {
                    	clicker.chat("/viewcps " + clicked.getName());
                    	return;
                    }
                    if (clicker.getInventory().getItemInHand().getType().equals(Material.PACKED_ICE)) {
                    	clicker.chat("/freeze " + clicked.getName());
                    	return;
                    }
                }
            }
    	}
    }

    public String getFormattedDuration(final Duel duel) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duel.getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duel.getDuration()) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
