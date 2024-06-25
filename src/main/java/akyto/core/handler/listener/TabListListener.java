package akyto.core.handler.listener;

import akyto.spigot.handler.PacketHandler;
import akyto.core.Core;
import akyto.core.rank.RankEntry;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Comparator;
import java.util.UUID;

public class TabListListener implements PacketHandler {

    static {
        UUID fakeUUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        if (Core.API.isDebug()) {
            Core.API.getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    Player player = event.getPlayer();

                    // Add fake
//                    PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
//                    PacketPlayOutPlayerInfo.PlayerInfoData[] data = new PacketPlayOutPlayerInfo.PlayerInfoData[6];
//                    for (int i = 0; i < 6; i++) {
//
//                        ChatColor color = PacketPlayOutPlayerInfo.ConnectionBars.values()[i].asColor();
//                        String col = ChatColor.translate(String.valueOf(color));
//                        data[i] = new PacketPlayOutPlayerInfo.PlayerInfoData(
//                                new GameProfile(
//                                        UUID.randomUUID(),
//                                        "TabListFake"
//                                ),
//                                PacketPlayOutPlayerInfo.ConnectionBars.values()[i].asPing(),
//                                WorldSettings.EnumGamemode.SURVIVAL,
//                                new ChatComponentText(col + "Test " + i)
//                        );
//                        packet.addFakePlayer(data[i]);
//
//                    }
//                    player.sendPacket(packet);

                    // Remove one
                    Core.API.getServer().getScheduler().runTaskLaterAsynchronously(
                            Core.API,
                            () -> {
                                // Clear default tab list
                                PacketPlayOutPlayerInfo clearPacket = new PacketPlayOutPlayerInfo(
                                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                                        () -> Core.API.getServer().getOnlinePlayers().stream()
                                                .map(online -> ((CraftPlayer) online).getHandle())
                                                .iterator()

                                );
                                Core.API.getServer().getOnlinePlayers().forEach(
                                        online -> online.sendPacket(clearPacket)
                                );

                                // Add back players in order
                                PacketPlayOutPlayerInfo addPacket = new PacketPlayOutPlayerInfo(
                                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                                        () -> Core.API.getServer().getOnlinePlayers().stream()
                                                .sorted(Comparator.comparingInt(a -> {
                                                    RankEntry rank = Core.API.getManagerHandler().getProfileManager().getRank(((Player) a).getUniqueId());
                                                    return rank == null ? 0 : rank.getPower();
                                                }).reversed())
                                                .map(online -> ((CraftPlayer) online).getHandle())
                                                .iterator()

                                );
                                Core.API.getServer().getOnlinePlayers().forEach(
                                        online -> online.sendPacket(addPacket)
                                );

                            },
                            20L * 4
                    );
                }
            }, Core.API);
        }
    }

    @Override
    public void handleReceivedPacket(PlayerConnection connection, Packet packet) {
        if (packet instanceof PacketPlayOutPlayerInfo) {
            PacketPlayOutPlayerInfo playerInfo = (PacketPlayOutPlayerInfo) packet;
//            System.out.println("Received " + playerInfo);
        }
    }

    @Override
    public void handleSentPacket(PlayerConnection connection, Packet packet) {
        if (packet instanceof PacketPlayOutPlayerInfo) {
            PacketPlayOutPlayerInfo playerInfo = (PacketPlayOutPlayerInfo) packet;
//            System.out.println("Sent " + playerInfo);
        }
    }
}
