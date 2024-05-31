package gym.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import gym.core.Core;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Utils {
	
	public static void sendServer(final Player player, final String type, final String server) {
	    ByteArrayDataOutput out = ByteStreams.newDataOutput();
	    out.writeUTF(type);
	    out.writeUTF(server);
	    player.sendPluginMessage(Core.API, "BungeeCord", out.toByteArray());
	}
	
	public static String translate(final String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public static String DateFormat(final Calendar cal) {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cal.getTime());
	}
	
	public static String formatTime(final long cooldown, final double dividend) {
		final double time = cooldown / dividend;
		final DecimalFormat df = new DecimalFormat("#.#");
		return df.format(time);
	}
	
	public static CompletableFuture<Boolean> checkNameMCLikeAsync(Core main, UUID uuid) {
	    final String uri = "https://api.namemc.com/server/" + main.getNamemcURL() + "/likes?profile=" + uuid;

	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            URL url = new URL(uri);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("GET");
	            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
	            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String inputLine;
	            StringBuilder response = new StringBuilder();
	            while ((inputLine = reader.readLine()) != null) {
	                response.append(inputLine);
	            }
	            reader.close();
	            String responseString = response.toString();
	            if (responseString.equalsIgnoreCase("true")) {
	                return true;
	            } else if (responseString.equalsIgnoreCase("false")) {
	                return false;
	            } else {
	                throw new IOException("Invalid response from URL: " + uri);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        }
	    });
	}
	
	public static UUID getUUID(String playerName) {
		Player target = Core.API.getServer().getPlayer(playerName);
		if (target != null)
			return target.getUniqueId();
		return Core.API.getServer().getOfflinePlayer(playerName).getUniqueId();
	}
}
