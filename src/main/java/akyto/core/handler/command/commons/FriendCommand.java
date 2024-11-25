package akyto.core.handler.command.commons;

import akyto.core.Core;
import akyto.core.handler.loader.Message;
import akyto.core.handler.manager.ProfileManager;
import akyto.core.profile.Profile;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendCommand {

    @Command(name = "friend", aliases= {"friends", "f", "amigos", "ami"}, inGameOnly = true)
    public void friend(final CommandArgs arg) {
        final Player sender = arg.getPlayer();
        final String[] args = arg.getArgs();
        final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(sender.getUniqueId());
        final Message msg = Core.API.getLoaderHandler().getMessage();
        final ProfileManager profileManager = Core.API.getManagerHandler().getProfileManager();
        if (args.length == 0) {
            if (profile.getFriends().isEmpty()) {
                sender.sendMessage(msg.getNoFriend());
                return;
            }
            StringBuilder builderOnline = new StringBuilder();
            StringBuilder builderOffline = new StringBuilder();
            profile.getFriends().forEach(uuid -> {
                if (builderOnline.length() > 0) builderOnline.append(ChatColor.GRAY).append(", ");
                if (builderOffline.length() > 0) builderOnline.append(ChatColor.GRAY).append(", ");
                if (Bukkit.getPlayer(uuid) != null) builderOnline.append(ChatColor.GREEN).append(CoreUtils.getName(uuid));
                if (Bukkit.getPlayer(uuid) == null) builderOffline.append(ChatColor.RED).append(CoreUtils.getName(uuid));
            });
            msg.getFriendsList().forEach(str -> {
                sender.sendMessage(str
                        .replace("%friendsOnline%", builderOnline.toString())
                        .replace("%friendsOffline%", builderOffline.toString())
                );
            });
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + args[1] + " not found.");
                    return;
                }
                if (profile.getFriends().contains(target.getUniqueId())) {
                    sender.sendMessage(msg.getAlreadyFriend().replace("%player%", args[1]));
                    return;
                }
                if (profileManager.getFriendsRequest().containsKey(sender.getUniqueId())) {
                    if (profileManager.getFriendsRequest().get(sender.getUniqueId()).equals(target.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "You have already sent a friend request to " + args[1]);
                        return;
                    }
                }
                Core.API.getManagerHandler().getProfileManager().getFriendsRequest().put(sender.getUniqueId(), target.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Your friend request has been sent to " + args[1]);
                final TextComponent accept = new TextComponent(ChatColor.DARK_GREEN + "Accept");
                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Click here to accept the friend request").create()));
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + sender.getName()));

                final TextComponent deny = new TextComponent(ChatColor.DARK_RED + "Deny");
                deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Click here to deny the friend request").create()));
                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + sender.getName()));

                final TextComponent request = new TextComponent(msg.getFriendRequest().replace("%player%", sender.getName()));
                request.addExtra(ChatColor.GRAY + " [");
                request.addExtra(accept);
                request.addExtra(ChatColor.GRAY + " / ");
                request.addExtra(deny);
                request.addExtra(ChatColor.GRAY + "]");

                target.spigot().sendMessage(request);
            }
            if (args[0].equalsIgnoreCase("accept")) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + args[1] + " not found.");
                    return;
                }
                if (profile.getFriends().contains(target.getUniqueId())) {
                    sender.sendMessage(msg.getAlreadyFriend().replace("%player%", args[1]));
                    return;
                }
                if (!profileManager.getFriendsRequest().containsKey(target.getUniqueId())) {
                    if (!profileManager.getFriendsRequest().get(target.getUniqueId()).equals(target.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "You have already sent a friend request to " + args[1]);
                        return;
                    }
                }
                if (!profileManager.getFriendsRequest().containsKey(target.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + args[1] + " doesn't have sent any request to you!");
                    return;
                }
                if (profileManager.getFriendsRequest().containsKey(target.getUniqueId())) {
                    if (!profileManager.getFriendsRequest().get(target.getUniqueId()).equals(sender.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + args[1] + " doesn't have sent any request to you!");
                        return;
                    }
                }
                profile.getFriends().add(target.getUniqueId());
                profileManager.getProfiles().get(target.getUniqueId()).getFriends().add(sender.getUniqueId());
                sender.sendMessage(msg.getFriendAdded().replace("%player%", target.getName()));
                target.sendMessage(msg.getFriendAdded().replace("%player%", sender.getName()));
                profileManager.getFriendsRequest().remove(target.getUniqueId());
            }
            if (args[0].equalsIgnoreCase("deny")) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + args[1] + " not found.");
                    return;
                }
                if (profile.getFriends().contains(target.getUniqueId())) {
                    sender.sendMessage(msg.getAlreadyFriend().replace("%player%", args[1]));
                    return;
                }
                if (!profileManager.getFriendsRequest().containsKey(target.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + args[1] + " doesn't have sent any request to you!");
                    return;
                }
                if (profileManager.getFriendsRequest().containsKey(sender.getUniqueId())) {
                    if (!profileManager.getFriendsRequest().get(sender.getUniqueId()).equals(target.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + args[1] + " doesn't have sent any request to you!");
                        return;
                    }
                }
                sender.sendMessage(msg.getFriendDeny().replace("%player%", target.getName()));
                target.sendMessage(ChatColor.RED + args[1] + " have deny your friend request!");
                profileManager.getFriendsRequest().remove(target.getUniqueId());
            }
            if (args[0].equalsIgnoreCase("remove")) {
                final Player target = Bukkit.getPlayer(args[1]);
                final UUID targetUUID = target == null ? Bukkit.getOfflinePlayer(args[1]).getUniqueId() : target.getUniqueId();
                if (!profile.getFriends().contains(targetUUID)) {
                    sender.sendMessage(ChatColor.RED + args[1] + " isn't your friend!");
                    return;
                }
                profile.getFriends().remove(targetUUID);
                if (target != null) {
                    profileManager.getProfiles().get(target.getUniqueId()).getFriends().remove(sender.getUniqueId());
                    target.sendMessage(msg.getRemovedFriend().replace("%player%", sender.getName()));
                }
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    Core.API.getRedis().lrem("player:" + sender.getUniqueId() + ":friends", 1, String.valueOf(targetUUID));
                    Core.API.getRedis().lrem("player:" + targetUUID + ":friends", 1, String.valueOf(sender.getUniqueId()));
                });
                future.whenCompleteAsync((t, u) -> {
                    sender.sendMessage(msg.getRemovedFriend().replace("%player%", CoreUtils.getName(target == null ? Bukkit.getOfflinePlayer(args[1]).getUniqueId() : target.getUniqueId())));
                });
            }
        }
    }
}
