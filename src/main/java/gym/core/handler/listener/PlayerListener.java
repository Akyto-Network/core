package gym.core.handler.listener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Lists;

import gym.core.Core;
import gym.core.chat.ChatState;
import gym.core.profile.Profile;
import gym.core.punishment.BanEntry;
import gym.core.punishment.MuteEntry;
import gym.core.rank.RankEntry;
import gym.core.utils.Utils;
import gym.core.utils.database.DatabaseType;
import kezukdev.akyto.duel.Duel;
import kezukdev.akyto.profile.ProfileState;

public class PlayerListener implements Listener {
	
	private final Core main;
	
	public PlayerListener(final Core main) {
		this.main = main;
	}
	
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
    	if (this.main.getLoaderHandler().getSettings().isBungeeCord()) {
    		if (!this.main.getLoaderHandler().getMessage().getBungeeIps().contains(event.getRealAddress().getHostAddress())) {
            	event.disallow(PlayerLoginEvent.Result.KICK_OTHER, this.main.getLoaderHandler().getMessage().getKickWhitelistProxy());
            	return;
    		}
    	}
		if (this.main.getManagerHandler().getPunishmentManager().getBanned().containsKey(event.getPlayer().getUniqueId())) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			final BanEntry ban = this.main.getManagerHandler().getPunishmentManager().getBanned().get(event.getPlayer().getUniqueId());
			Date todayGlobal = new Date();
			try {
				Date banExpiresOn = sdf.parse(this.main.getManagerHandler().getPunishmentManager().getBanned().get(event.getPlayer().getUniqueId()).getExpiresOn());
				if (banExpiresOn != null && banExpiresOn.before(todayGlobal)) {
					this.main.getManagerHandler().getPunishmentManager().getBanned().remove(event.getPlayer().getUniqueId());
				} else if (banExpiresOn != null && !banExpiresOn.equals(todayGlobal)) {
				    if (this.main.getLoaderHandler().getSettings().isTryToConnect()) {
						Bukkit.getOnlinePlayers().forEach(players -> {
							if (players.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce()) && !this.main.getManagerHandler().getProfileManager().getRank(event.getPlayer().getUniqueId()).equals(this.main.getManagerHandler().getRankManager().getRanks().get("default")) && event.getPlayer().hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {
								players.sendMessage(this.main.getLoaderHandler().getMessage().getTryToConnect().replace("%banned%", event.getPlayer().getName()).replace("%expires%", ban.getExpiresOn()).replace("%judge%", ban.getJudge()).replace("%reason%", ban.getReason()));
							}
						});	
				    }	
				    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, (main.getLoaderHandler().getMessage().getBanDisconnect().replace("%expires%", ban.getExpiresOn()).replace("%reason%", ban.getReason()).replace("%judge%", ban.getJudge())));
				    return;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
    }
    
	@EventHandler
	public void onClick(final PlayerInteractEvent e){
	    if (e.getAction() == Action.LEFT_CLICK_AIR) {
	      Player player = e.getPlayer();
	      Profile wp = this.main.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId());
	      if ((player.getTargetBlock(null, 100).getLocation().distance(player.getLocation()) < 6.0D) && (wp.lastBlockInteraction > System.currentTimeMillis()) && (wp.clicks[0] >= 10)) {
	    	  e.setCancelled(true);
	    	  e.setUseInteractedBlock(Result.DENY); // IDK if it works but it seems to be the only way
	    	  e.setUseItemInHand(Result.DENY);
	    	  return;
	      }
	      if (wp.clicks[0] > wp.maxClick) {
	    	  wp.maxClick = wp.clicks[0];
	      }
	      wp.clicks[0] += 1;
	    } else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
	      Player player = e.getPlayer();
	      Profile wp = this.main.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId());
	      wp.lastBlockInteraction = (System.currentTimeMillis() + 5000L);
	    }
	}
    
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		event.setJoinMessage(null);
		this.main.getManagerHandler().getProfileManager().createProfile(event.getPlayer().getUniqueId());
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerLeft(final PlayerQuitEvent event) {
		event.setQuitMessage(null);
		if (this.main.getDatabaseType().equals(DatabaseType.MYSQL)) {
			this.main.getManagerHandler().getProfileManager().exitAsync(event.getPlayer().getUniqueId());
		}
		if (this.main.getLoaderHandler().getSettings().isStaffNotifications()) {
			if (!Bukkit.getOnlinePlayers().isEmpty()) {
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce()) && !this.main.getManagerHandler().getProfileManager().getRank(event.getPlayer().getUniqueId()).equals(this.main.getManagerHandler().getRankManager().getRanks().get("default")) && event.getPlayer().hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {
						player.sendMessage(this.main.getLoaderHandler().getMessage().getStaffAnnounce().replace("%rank%", this.main.getManagerHandler().getProfileManager().getRank(event.getPlayer().getUniqueId()).getPrefix()).replace("%rankColor%", Utils.translate(this.main.getManagerHandler().getProfileManager().getRank(event.getPlayer().getUniqueId()).getColor())).replace("%player%", event.getPlayer().getName()).replace("%type%", "left"));
					}
				});	
			}	
		}
	}
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.main.getManagerHandler().getProfileManager().getFrozed().contains(event.getPlayer().getUniqueId())) {
            if (event.getPlayer().getOpenInventory().getType() != InventoryType.DISPENSER) {
                event.setCancelled(true);
            }
        }
        if (this.main.getPracticeAPI().getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).getProfileState().equals(ProfileState.MOD)) {
        	if (event.getItem().getType().equals(Material.NETHER_STAR)) {
        		if (this.main.getPracticeAPI().getDuels().isEmpty()) {
        			event.getPlayer().sendMessage(ChatColor.RED + "0 player is in match!");
        			return;
        		}
        		final List<UUID> playersInMatch = new ArrayList<>();
        		this.main.getPracticeAPI().getDuels().forEach(duel -> {
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
        		this.main.getLoaderHandler().getMessage().getRandomTeleport().forEach(msg -> {
        			event.getPlayer().sendMessage(msg.replace("%target%", Bukkit.getPlayer(playersInMatch.get(0)).getName()).replace("%playerOne%", Bukkit.getPlayer(new ArrayList<>(duel.getFirst()).get(0)).getName()).replace("%playerTwo%", Bukkit.getPlayer(new ArrayList<>(duel.getSecond()).get(0)).getName()).replace("%matchLadder%", ChatColor.stripColor(duel.getKit().displayName())).replace("%matchDuration%", this.getFormattedDuration(duel)));
        		});
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
    
    public String getFormattedDuration(final Duel duel) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duel.getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duel.getDuration()) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
    	if (this.main.getPracticeAPI().getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).getProfileState().equals(ProfileState.MOD)) {
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
	
	@EventHandler
	public void onPlayerKick(final PlayerKickEvent event) {
		this.onPlayerLeft(new PlayerQuitEvent(event.getPlayer(), null));
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerTalk(final AsyncPlayerChatEvent event) {
		if (this.main.getManagerHandler().getPunishmentManager().getMuted().containsKey(event.getPlayer().getUniqueId())) {
			final MuteEntry mute = this.main.getManagerHandler().getPunishmentManager().getMuted().get(event.getPlayer().getUniqueId());
			Date todayGlobal = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			try {
				Date muteExpiresOn = sdf.parse(this.main.getManagerHandler().getPunishmentManager().getMuted().get(event.getPlayer().getUniqueId()).getExpiresOn());
				if (muteExpiresOn != null && muteExpiresOn.before(todayGlobal)) {
					this.main.getManagerHandler().getPunishmentManager().getMuted().remove(event.getPlayer().getUniqueId());
				} else if (muteExpiresOn != null && !muteExpiresOn.equals(todayGlobal)) {
				    event.getPlayer().sendMessage(this.main.getLoaderHandler().getMessage().getMuteCancel().replace("%reason%", mute.getReason()).replace("%judge%", mute.getJudge()).replace("%expires%", mute.getExpiresOn()));
				    event.setCancelled(true);
				    return;
				}
			} catch (ParseException e) { e.printStackTrace(); }	
		}
		if (this.main.getManagerHandler().getServerManager().getChatState().equals(ChatState.CLOSED)) {
			if (!event.getPlayer().hasPermission(this.main.getLoaderHandler().getPermission().getBypassChatClosed())) {
				event.getPlayer().sendMessage(ChatColor.RED + "Sorry but the chat as currently closed!");
				event.setCancelled(true);
				return;	
			}
		}
		if (this.main.getLoaderHandler().getSettings().isChatCooldown() && !event.getPlayer().hasPermission(this.main.getLoaderHandler().getPermission().getBypassCooldownChat())) {
			if (this.main.getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).isChatCooldownActive()) {
				event.getPlayer().sendMessage(this.main.getLoaderHandler().getMessage().getChatCooldown().replace("%time%", Utils.formatTime(this.main.getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).getChatCooldown(), 1000.0d)));
				event.setCancelled(true);
				return;
			}	
		}
		if (!event.getPlayer().hasPermission(this.main.getLoaderHandler().getPermission().getBypassFilterChat())) {
			for (String filter : this.main.getLoaderHandler().getMessage().getFilteredText()) {
				if (event.getMessage().contains(filter)) {
					StringBuilder filtered = new StringBuilder();
					for (int i = 0; i < filter.length(); i++) {
						filtered.append("*");
					}
					String newMsg = event.getMessage().replace(filter, filtered.toString());
					event.setMessage(newMsg);
				}
			}	
		}
		if (this.main.getLoaderHandler().getSettings().isChatCooldown() && !event.getPlayer().hasPermission(this.main.getLoaderHandler().getPermission().getBypassCooldownChat())) {
			this.main.getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).applyChatCooldown(this.main.getManagerHandler().getServerManager().getChatPriority().getTime());	
		}
		final RankEntry rank = this.main.getManagerHandler().getProfileManager().getRank(event.getPlayer().getUniqueId());
		final String prefix = Utils.translate(rank.getPrefix());
		final String color = Utils.translate(rank.getColor());
		if (event.getMessage().startsWith(this.main.getLoaderHandler().getMessage().getScSymbol()) && event.getPlayer().hasPermission("akyto.staff")) {
			Bukkit.getOnlinePlayers().forEach(player -> {
				if (player.hasPermission("akyto.staff")) {
					player.sendMessage(this.main.getLoaderHandler().getMessage().getScFormat()
							.replace("%prefix%", prefix + (rank.getHasSpaceBetweenColor() ? " " : ""))
							.replace("%rankColor%", color).replace("%player%", event.getPlayer().getName())
							.replace("%msg%", event.getMessage().replaceFirst("!", "")));
				}
			});
			event.setCancelled(true);
			return;
		}
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (event.getMessage().contains(player.getName())) {
				List<Player> p = Lists.newArrayList(Bukkit.getOnlinePlayers());
				p.remove(Bukkit.getPlayer(player.getName()));
				player.sendMessage(Utils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
						.replace("%prefix%", prefix + (rank.getHasSpaceBetweenColor() ? " " : ""))
						.replace("%rankColor%", color)
						.replace("%player%", event.getPlayer().getName())
						.replace("%likeTag%",  this.main.getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
						.replace("%msg%", event.getMessage().replace(player.getName(), ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + ChatColor.ITALIC + player.getName() + ChatColor.RESET))));
				player.playSound(player.getLocation(), Sound.FIZZ, 1f, 1f);
				p.forEach(ppl -> ppl.sendMessage(Utils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
						.replace("%prefix%", prefix + (rank.getHasSpaceBetweenColor() ? " " : ""))
						.replace("%rankColor%", color)
						.replace("%player%", event.getPlayer().getName())
						.replace("%likeTag%",  this.main.getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
						.replace("%msg%", event.getMessage().replace(player.getName(), player.getName())))));
				event.setCancelled(true);
				return;
			}
		});
		event.setFormat(Utils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
				.replace("%prefix%", prefix + (rank.getHasSpaceBetweenColor() ? " " : ""))
				.replace("%rankColor%", color)
				.replace("%player%", "%1$s")
				.replace("%likeTag%",  this.main.getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
				.replace("%msg%", "%2$s")));
	}

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getName() != null && clickedInventory.getName().startsWith(ChatColor.GRAY + "Viewing CPS »")) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BOOK) {
                String[] nameParts = clickedInventory.getName().split("» ");
                if (nameParts.length > 1) {
                    String name = ChatColor.stripColor(nameParts[1]);
                    if (name.equalsIgnoreCase(event.getWhoClicked().getName())) {
                        event.getWhoClicked().closeInventory();
                        Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).sendMessage(ChatColor.RED + "You cannot punish yourself!");
                        return;
                    }
                    Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).chat("/ban " + name + " 30d Autoclicking");
                    Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).sendMessage(ChatColor.RED + name + ChatColor.WHITE + " has been punished with viewcps.");
                    event.getWhoClicked().closeInventory();
                }
            }
        }
    }
}
