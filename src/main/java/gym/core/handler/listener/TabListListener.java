package gym.core.handler.listener;

import akyto.spigot.handler.PacketHandler;
import com.mojang.authlib.GameProfile;
import gym.core.Core;
import net.minecraft.server.v1_8_R3.WorldSettings;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class TabListListener implements PacketHandler {

    static {
        UUID fakeUUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        if (Core.API.isDebug()) {
            Core.API.getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    Player player = event.getPlayer();
                    PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
                    for (int i = 0; i < 6; i++) {

                        ChatColor color = PacketPlayOutPlayerInfo.ConnectionBars.values()[i].asColor();
                        String col = ChatColor.translate(String.valueOf(color));
                        packet.addFakePlayer(
                                new GameProfile(
                                        fakeUUID,
                                        "TabListFake"
                                ),
                                PacketPlayOutPlayerInfo.ConnectionBars.values()[i].asPing(),
                                WorldSettings.EnumGamemode.SURVIVAL,
                                new ChatComponentText(col + "Test " + i)
                        );

                    }
                    player.sendPacket(packet);
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
