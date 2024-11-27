package akyto.core.handler.listener;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import akyto.core.disguise.DisguiseEntry;
import akyto.core.handler.manager.TagManager;
import akyto.core.tag.TagEntry;
import akyto.core.whitelist.WhitelistState;
import co.aikar.idb.DB;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import akyto.core.Core;
import akyto.core.chat.ChatState;
import akyto.core.profile.Profile;
import akyto.core.punishment.cache.BanEntry;
import akyto.core.punishment.cache.MuteEntry;
import akyto.core.rank.RankEntry;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.database.DatabaseType;

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
		if (!Core.API.getManagerHandler().getServerManager().getWhitelistState().equals(WhitelistState.OFF)) {
			if (Core.API.getBlacklistWhitelist().contains(event.getPlayer().getName().toLowerCase())) {
				event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Core.API.getLoaderHandler().getMessage().getWhitelistKickBlacklist());
				return;
			}
			if (Core.API.getManagerHandler().getServerManager().getWhitelistState().equals(WhitelistState.ON_LIST)) {
				if (!Core.API.getWhitelisted().contains(event.getPlayer().getName().toLowerCase())) {
					event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Core.API.getLoaderHandler().getMessage().getWhitelistKickOnList());
					return;
				}
			}
			if (Core.API.getManagerHandler().getServerManager().getWhitelistState().equals(WhitelistState.HAVE_RANK)) {
				if (!Core.API.getWhitelisted().contains(event.getPlayer().getName().toLowerCase())){
					try {
						String rank = "default";
                        try {
                            if (Core.API.getMySQL().existPlayerManagerAsync(event.getPlayer().getUniqueId()).get()) {
								rank = DB.getFirstRow("SELECT rank FROM playersdata WHERE name=?", event.getPlayer().getName()).getString("rank");
                            }
                        } catch (InterruptedException | ExecutionException e) { throw new RuntimeException(e); }
						if (!Core.API.getManagerHandler().getRankManager().getRanks().get(rank).hasRankWhitelist()) {
							event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Core.API.getLoaderHandler().getMessage().getWhitelistKickRank());
							return;
						}
					} catch (SQLException e) { throw new RuntimeException(e); }
				}
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
							if (players.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())
									&& player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {
								players.sendMessage(this.main.getLoaderHandler().getMessage().getTryToConnect()
										.replace("%banned%", player.getName())
										.replace("%expires%", ban.getExpiresOn())
										.replace("%judge%", ban.getJudge())
										.replace("%reason%", ban.getReason())
								);
							}
						});	
				    }	
				    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, (main.getLoaderHandler().getMessage().getBanDisconnect()
							.replace("%expires%", ban.getExpiresOn())
							.replace("%reason%", ban.getReason())
							.replace("%judge%", ban.getJudge())
						)
					);
                }
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
    }

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		event.setJoinMessage(null);
		if (!Core.API.getManagerHandler().getProfileManager().getDisguised().isEmpty()) {
			Bukkit.getScheduler().runTaskLater(Core.API, () -> {
				final List<Player> pls = new ArrayList<>();
				for (Map.Entry<UUID, DisguiseEntry> entry : Core.API.getManagerHandler().getProfileManager().getDisguised().entrySet()) {
					final Player disguised = Bukkit.getPlayer(entry.getKey());
					CoreUtils.disguise(event.getPlayer(), disguised, entry.getValue());
					pls.add(disguised);
					event.getPlayer().hidePlayer(disguised);
				}
				Bukkit.getScheduler().runTaskLater(Core.API, () -> {
					pls.forEach(players -> event.getPlayer().showPlayer(players));
				}, 20L);
			}, 2L);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerLeft(final PlayerQuitEvent event) {
		event.setQuitMessage(null);
		final Player leaver = event.getPlayer();
		if (this.main.getLoaderHandler().getSettings().isStaffNotifications() && !this.main.isShutdown()) {
			final RankEntry rank = this.main.getManagerHandler().getProfileManager().getRank(leaver.getUniqueId());
			Bukkit.getOnlinePlayers().forEach(player -> {
				if (player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())
						&& !rank.equals(Core.API.getManagerHandler().getRankManager().getRanks().get("default"))
						&& leaver.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {

					player.sendMessage(this.main.getLoaderHandler().getMessage().getStaffAnnounce()
							.replace("%rank%", rank.getPrefix())
							.replace("%rankColor%", CoreUtils.translate(rank.getColor()))
							.replace("%player%", leaver.getName())
							.replace("%type%", "left"));
				}
			});
		}
		if (Core.API.getManagerHandler().getProfileManager().getDisguised().containsKey(leaver.getUniqueId())){
			Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().remove(Core.API.getManagerHandler().getProfileManager().getDisguised().get(leaver.getUniqueId()).getName());
			Core.API.getManagerHandler().getProfileManager().getDisguised().remove(leaver.getUniqueId());
		}
		if (this.main.getDatabaseType().equals(DatabaseType.MYSQL)){
			if (!this.main.isShutdown()) this.main.getDatabaseSetup().exitAsync(leaver.getUniqueId());
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
			final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(event.getPlayer().getUniqueId());
			int clicks = profile.getClicks();
			profile.setClicks(++clicks);
			long currentTime = System.currentTimeMillis();
			if (currentTime - profile.getLastClickTime() >= 1000) {
				profile.setCps(clicks);
				profile.setClicks(0);
				profile.setLastClickTime(currentTime);
				if (!Core.API.getBypassCpsCap().contains(event.getPlayer().getName())) {
					profile.setAllowClick(profile.getCps() < Core.API.getLoaderHandler().getSettings().getMaximumCps());
				}
			}
		}
		if (this.main.getManagerHandler().getProfileManager().getFrozen().contains(event.getPlayer().getUniqueId())) {
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

	// Depend on the spigot build
//	@EventHandler
//	public void onPlayerKick(final PlayerKickEvent event) {
//		this.onPlayerLeft(new PlayerQuitEvent(event.getPlayer(), null));
//	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerTalk(final AsyncPlayerChatEvent event) {
		final Player pls = event.getPlayer();

		if (pls.isOp() || pls.hasPermission("akyto.colorchat")) {
			event.setMessage(CoreUtils.translate(event.getMessage()));
		}

		if (this.main.getManagerHandler().getPunishmentManager().getMuted().containsKey(pls.getUniqueId())) {
			final MuteEntry mute = this.main.getManagerHandler().getPunishmentManager().getMuted().get(pls.getUniqueId());
			Date todayGlobal = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			try {
				Date muteExpiresOn = sdf.parse(mute.getExpiresOn());
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
				pls.sendMessage(this.main.getLoaderHandler().getMessage().getChatCooldown().replace("%time%", CoreUtils.formatTime(this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).getChatCooldown(), 1000.0d)));
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
		String prefix = CoreUtils.translate(rank.getPrefix());
		String color = CoreUtils.translate(rank.getColor());
		boolean spacer = rank.hasSpaceBetweenColor();
		if (Core.API.getManagerHandler().getProfileManager().getDisguised().containsKey(pls.getUniqueId())) {
			prefix = "";
			spacer = false;
			color = ChatColor.GREEN.toString();
		}
		String finalPrefix = prefix;
		boolean finalSpacer = spacer;
		String finalColor = color;
		if (event.getMessage().startsWith(this.main.getLoaderHandler().getMessage().getScSymbol()) && pls.hasPermission("akyto.staff")) {
			Bukkit.getOnlinePlayers().stream()
					.filter(player -> player.hasPermission("akyto.staff"))
					.forEach(player -> {
						player.sendMessage(this.main.getLoaderHandler().getMessage().getScFormat()
								.replace("%prefix%", finalPrefix + (finalSpacer ? " " : ""))
								.replace("%rankColor%", finalColor).replace("%player%", pls.getDisplayName())
								.replace("%msg%", event.getMessage().replaceFirst("!", "")));
			});
			event.setCancelled(true);
			return;
		}
		List<Player> mentionedPlayers = new ArrayList<>();
		final Profile profile = this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId());
		final TagEntry tagEntry = this.main.getManagerHandler().getTagManager().getTags().get(profile.getTag());
		Bukkit.getOnlinePlayers().stream()
				.filter(player -> event.getMessage().contains(player.getName()))
				.forEach(player -> {
					mentionedPlayers.add(player);
					String message = CoreUtils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
							.replace("%prefix%", finalPrefix + (finalSpacer ? " " : ""))
							.replace("%rankColor%", finalColor)
							.replace("%player%", event.getPlayer().getDisplayName())
							.replace("%tag%", tagEntry != null ? " " + tagEntry.getPrefix() : "")
							.replace("%likeTag%", this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
							.replace("%msg%", event.getMessage().replace(player.getName(), ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + ChatColor.ITALIC + player.getName() + ChatColor.RESET)));

					player.sendMessage(message);
					player.playSound(player.getLocation(), Sound.FIZZ, 1f, 1f);
				});
		if (!mentionedPlayers.isEmpty()) {
			String finalMessage = CoreUtils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
					.replace("%prefix%", finalPrefix + (finalSpacer ? " " : ""))
					.replace("%rankColor%", finalColor)
					.replace("%player%", event.getPlayer().getDisplayName())
					.replace("%tag%", tagEntry != null ? " " + tagEntry.getPrefix() : "")
					.replace("%likeTag%", this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
					.replace("%msg%", event.getMessage()));

			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if (!mentionedPlayers.contains(onlinePlayer)) {
					onlinePlayer.sendMessage(finalMessage);
				}
			}
			event.setCancelled(true);
			return;
		}
		event.setFormat(CoreUtils.translate(this.main.getLoaderHandler().getMessage().getChatFormat()
				.replace("%prefix%", finalPrefix + (finalSpacer ? " " : ""))
				.replace("%rankColor%", finalColor)
				.replace("%player%", "%1$s")
				.replace("%tag%", tagEntry != null ? " " + tagEntry.getPrefix() : "")
				.replace("%likeTag%",  this.main.getManagerHandler().getProfileManager().getProfiles().get(pls.getUniqueId()).isLikeNameMC() ? " " + this.main.getLoaderHandler().getMessage().getNameMCLikeTag() : "")
				.replace("%msg%", "%2$s"))
		);
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		if (event.getPlayer().isSprinting()) {
			event.setCancelled(true);
		}
	}
	
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
		final Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
		if (clickedInventory == null) return;
        if (clickedInventory.getName() != null && clickedInventory.getName().startsWith(ChatColor.GRAY + "Viewing CPS »")) {
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
		if (clickedInventory.equals(Core.API.getManagerHandler().getInventoryManager().getCommonTags().get(player.getUniqueId()))) {
			event.setResult(Event.Result.DENY);
			event.setCancelled(true);
			if (event.getCurrentItem() == null) return;
			if (event.getCurrentItem().getType().equals(Material.AIR)) return;
			final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId());
			if (event.getCurrentItem().getType().equals(Material.TRAP_DOOR) && event.getCurrentItem().getItemMeta().getDisplayName().contains("Remove")) {
				if (profile.getTag().equals("none")) {
					player.closeInventory();
					player.sendMessage(ChatColor.RED + "You doesn't have set any tags.");
					return;
				}
				profile.setTag("none");
				player.sendMessage(ChatColor.RED + "You have been removed your tag!");
				player.closeInventory();
				return;
			}
			final TagManager tagManager = Core.API.getManagerHandler().getTagManager();
			final String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
			final TagEntry tagEntry = tagManager.getTags().get(name);
			if (!profile.getTag().equals("none")) {
				if (tagManager.getTags().get(profile.getTag()).getPrefix().equals(tagEntry.getPrefix())) {
					player.closeInventory();
					player.sendMessage(ChatColor.RED + "You already have set this tag");
					return;
				}
			}
			if (player.hasPermission(tagEntry.getPermissions()) || profile.getPermissions().contains(tagEntry.getPermissions())) {
				profile.setTag(name);
				Core.API.getManagerHandler().getInventoryManager().generateCommonTagInventory(player.getUniqueId());
				player.sendMessage(ChatColor.GRAY + "You have correctly defined " + tagEntry.getPrefix() + ChatColor.GRAY + " as a tag");
				player.closeInventory();
				return;
			}
			if (profile.getTokens() < tagEntry.getPrice()) {
				player.closeInventory();
				final int tokenMissed = tagEntry.getPrice() - profile.getTokens();
				player.sendMessage(Core.API.getLoaderHandler().getMessage().getTokensMissing().replace("%tokens%", String.valueOf(tokenMissed)));
				return;
			}
			else {
				profile.setTokens(profile.getTokens() - tagEntry.getPrice());
				player.sendMessage(ChatColor.GREEN + "You have been bought " + tagEntry.getPrefix() + ChatColor.GREEN + " tag!");
				profile.setTag(name);
				profile.getPermissions().add(tagEntry.getPermissions());
				Core.API.getManagerHandler().getProfileManager().registerPermissions(player.getUniqueId());
			}
		}
    }
}
