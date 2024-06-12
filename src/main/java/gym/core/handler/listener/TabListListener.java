package gym.core.handler.listener;

import akyto.spigot.handler.PacketHandler;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;

public class TabListListener implements PacketHandler {

    @Override
    public void handleReceivedPacket(PlayerConnection connection, Packet packet) {
        if (packet instanceof PacketPlayOutPlayerInfo) {
            PacketPlayOutPlayerInfo playerInfo = (PacketPlayOutPlayerInfo) packet;
            System.out.println("Received " + playerInfo);
        }
    }

    @Override
    public void handleSentPacket(PlayerConnection connection, Packet packet) {
        if (packet instanceof PacketPlayOutPlayerInfo) {
            PacketPlayOutPlayerInfo playerInfo = (PacketPlayOutPlayerInfo) packet;
            System.out.println("Sent " + playerInfo);
        }
    }
}
