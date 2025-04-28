package akyto.core.handler.listener;

import akyto.core.Core;
import akyto.core.whitelist.WhitelistState;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {

    private Core main;

    public ServerListener(final Core main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPing(final ServerListPingEvent event) {
        if (!Core.API.getManagerHandler().getServerManager().getWhitelistState().equals(WhitelistState.OFF)) {
            event.setMaxPlayers(Bukkit.getServer().getOnlinePlayers().size());
            event.setMotd(main.getLoaderHandler().getMessage().getMotdWhitelist().get(0) + "\n" + this.main.getLoaderHandler().getMessage().getMotdWhitelist().get(1));
            return;
        }
        event.setMaxPlayers(Bukkit.getServer().getOnlinePlayers().size() + 1);
        event.setMotd(this.main.getLoaderHandler().getMessage().getMotdOpen().get(0) + "\n" + this.main.getLoaderHandler().getMessage().getMotdOpen().get(1));
    }

}
