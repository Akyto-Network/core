package gym.core.handler.listener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Lists;

import gym.core.Core;
import gym.core.chat.ChatState;
import gym.core.punishment.BanEntry;
import gym.core.punishment.MuteEntry;
import gym.core.rank.RankEntry;
import gym.core.utils.Utils;
import gym.core.utils.database.DatabaseType;

public class PlayerListener implements Listener {
	
	private final Core main;
	
	public PlayerListener(final Core main) {
		this.main = main;
	}
	
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
		final Player player = event.getPlayer();

    	if (this.main.getLoaderHandler().getSettings().isBungeeCord()) {
    		if (!this.main.getLoaderHandler().getMessage().getBungeeIps().contains(event.getRealAddress().getHostAddress())) {
            	event.disallow(PlayerLoginEvent.Result.KICK_OTHER, this.main.getLoaderHandler().getMessage().getKickWhitelistProxy());
            	return;
    		}
    	}
		if (this.main.getManagerHandler().getPunishmentManager().getBanned().containsKey(player.getUniqueId())) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			final BanEntry ban = this.main.getManagerHandler().getPunishmentManager().getBanned().get(player.getUniqueId());
			Date todayGlobal = new Date();
			try {
				Date banExpiresOn = sdf.parse(this.main.getManagerHandler().getPunishmentManager().getBanned().get(player.getUniqueId()).getExpiresOn());
				if (banExpiresOn != null && banExpiresOn.before(todayGlobal)) {
					this.main.getManagerHandler().getPunishmentManager().getBanned().remove(player.getUniqueId());
				} else if (banExpiresOn != null && !banExpiresOn.equals(todayGlobal)) {
				    if (this.main.getLoaderHandler().getSettings().isTryToConnect()) {
						Bukkit.getOnlinePlayers().forEach(players -> {
							if (players.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce()) && !this.main.getManagerHandler().getProfileManager().getRank(player.getUniqueId()).equals(this.main.getManagerHandler().getRankManager().getRanks().get("default")) && player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {
								players.sendMessage(this.main.getLoaderHandler().getMessage().getTryToConnect().replace("%banned%", player.getName()).replace("%expires%", ban.getExpiresOn()).replace("%judge%", ban.getJudge()).replace("%reason%", ban.getReason()));
							}
						});	
				    }	
				    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, (main.getLoaderHandler().getMessage().getBanDisconnect().replace("%expires%", ban.getExpiresOn()).replace("%reason%", ban.getReason()).replace("%judge%", ban.getJudge())));
                }
			} catch (ParseException e) {
				e.printStackTrace();
			}
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
			Bukkit.getOnlinePlayers().forEach(player -> {
				if (player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce()) && !this.main.getManagerHandler().getProfileManager().getRank(event.getPlayer().getUniqueId()).equals(this.main.getManagerHandler().getRankManager().getRanks().get("default")) && event.getPlayer().hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {
					player.sendMessage(this.main.getLoaderHandler().getMessage().getStaffAnnounce().replace("%rank%", this.main.getManagerHandler().getProfileManager().getRank(event.getPlayer().getUniqueId()).getPrefix()).replace("%rankColor%", Utils.translate(this.main.getManagerHandler().getProfileManager().getRank(event.getPlayer().getUniqueId()).getColor())).replace("%player%", event.getPlayer().getName()).replace("%type%", "left"));
				}
			});
		}
	}
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.main.getManagerHandler().getProfileManager().getFrozed().contains(event.getPlayer().getUniqueId())) {
            if (event.getPlayer().getOpenInventory().getType() != InventoryType.DISPENSER) {
                event.setCancelled(true);
            }
        }
    }
    
    
	@EventHandler(priority=EventPriority.LOW)
	public void PlayerPlaceBlockEvent(final BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
		event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void PlayerBreakBlockEvent(final BlockBreakEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerKick(final PlayerKickEvent event) {
		this.onPlayerLeft(new PlayerQuitEvent(event.getPlayer(), null));
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerTalk(final AsyncPlayerChatEvent event) {
		final Player pls = event.getPlayer();

		if (pls.isOp() || pls.hasPermission("akyto.colorchat")) {
			event.setMessage(Utils.translate(event.getMessage()));
		}

		if (this.main.getManagerHandler().getPunishmentManager().getMuted().containsKey(pls.getUniqueId())) {
			final MuteEntry mute = this.main.getManagerHandler().getPunishmentManager().getMuted().get(pls.getUniqueId());
			Date todayGlobal = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			try {
				Date muteExpiresOn = sdf.parse(this.main.getManagerHandler().getPunishmentManager().getMuted().get(pls.getUniqueId()).getExpiresOn());
				if (muteExpiresOn != null && muteExpiresOn.before(todayGlobal)) {
					this.main.getManagerHandler().getPunishmentManager().getMuted().remove(pls.getUniqueId());
				} else if (muteExpiresOn != null && !muteExpiresOn.equals(todayGlobal)) {
				    pls.sendMessage(this.main.getLoaderHandler().getMessage().getMuteCancel().replace("%reason%", mute.getReason()).replace("%judge%", mute.getJudge()).replace("%expires%", mute.getExpiresOn()));
				    event.setCancelled(true);
				    return;
				}
			} catch (ParseException e) { e.printStackTrace(); }	
		}
		if (this.main.getManagerHandler().getServerManager().getChatState().equals(ChatState.CLOSED)) {
			if (!pls.hasPermission(this.main.getLoaderHandler().getPermission().getBypassChatClosed())) {
				pls.sendMessage(ChatColor.RED + "Sorry but the chat as currently closed!");
				event.setCancelled(true);
				return;	
			}
		}
		if (this.main.getLoaderHandler().getSettings().isChatCooldown() && !pls.hasPermission(this.main.getLoaderHandler().getPermission().getBypassCooldownChat())) {
			if (this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).isChatCooldownActive()) {
				pls.sendMessage(this.main.getLoaderHandler().getMessage().getChatCooldown().replace("%time%", Utils.formatTime(this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).getChatCooldown(), 1000.0d)));
				event.setCancelled(true);
				return;
			}	
		}
		if (!pls.hasPermission(this.main.getLoaderHandler().getPermission().getBypassFilterChat())) {
			for (String filter : this.main.getLoaderHandler().getMessage().getFilteredText()) {
				if (event.getMessage().contains(filter)) {
                    String newMsg = event.getMessage().replace(filter, "*".repeat(filter.length()));
					event.setMessage(newMsg);
				}
			}	
		}
		if (this.main.getLoaderHandler().getSettings().isChatCooldown() && !pls.hasPermission(this.main.getLoaderHandler().getPermission().getBypassCooldownChat())) {
			this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).applyChatCooldown(this.main.getManagerHandler().getServerManager().getChatPriority().getTime());
		}
		final RankEntry rank = this.main.getManagerHandler().getProfileManager().getRank(pls.getUniqueId());
		final String prefix = Utils.translate(rank.getPrefix());
		final String color = Utils.translate(rank.getColor());
		if (event.getMessage().startsWith(this.main.getLoaderHandler().getMessage().getScSymbol()) && pls.hasPermission("akyto.staff")) {
			Bukkit.getOnlinePlayers().stream()
					.filter(player -> player.hasPermission("akyto.staff"))
					.forEach(player -> {
						player.sendMessage(this.main.getLoaderHandler().getMessage().getScFormat()
								.replace("%prefix%", prefix + (rank.hasSpaceBetweenColor() ? " " : ""))
								.replace("%rankColor%", color).replace("%player%", pls.getName())
								.replace("%msg%", event.getMessage().replaceFirst("!", "")));
			});
			event.setCancelled(true);
			return;
		}
		Bukkit.getOnlinePlayers().stream()
				.filter(player -> event.getMessage().contains(player.getName()))
				.forEach(player -> {
					List<Player> p = Lists.newArrayList(Bukkit.getOnlinePlayers());
					p.remove(Bukkit.getPlayer(player.getName()));
					player.sendMessage(Utils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
							.replace("%prefix%", prefix + (rank.hasSpaceBetweenColor() ? " " : ""))
							.replace("%rankColor%", color)
							.replace("%player%", pls.getName())
							.replace("%likeTag%",  this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
							.replace("%msg%", event.getMessage().replace(player.getName(), ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + ChatColor.ITALIC + player.getName() + ChatColor.RESET))));
					player.playSound(player.getLocation(), Sound.FIZZ, 1f, 1f);
					p.forEach(ppl -> ppl.sendMessage(Utils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
							.replace("%prefix%", prefix + (rank.hasSpaceBetweenColor() ? " " : ""))
							.replace("%rankColor%", color)
							.replace("%player%", pls.getName())
							.replace("%likeTag%",  this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
							.replace("%msg%", event.getMessage()))));
					event.setCancelled(true);
		});
		event.setFormat(Utils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
				.replace("%prefix%", prefix + (rank.hasSpaceBetweenColor() ? " " : ""))
				.replace("%rankColor%", color)
				.replace("%player%", "%1$s")
				.replace("%likeTag%",  this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
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
