package akyto.core.utils;

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
import java.util.concurrent.atomic.AtomicReference;

import akyto.core.disguise.DisguiseEntry;
import akyto.core.particle.ParticleEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

import akyto.core.Core;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.json.JSONObject;

public class CoreUtils {

	public static UUID getUUID(String playerName) {
		Player target = Core.API.getServer().getPlayer(playerName);
		if (target != null) {
			return target.getUniqueId();
		}
		return Core.API.getServer().getOfflinePlayer(playerName).getUniqueId();
	}

	public static String getName(UUID playerId) {
		Player target = Core.API.getServer().getPlayer(playerId);
		if (target != null) {
			return target.getDisplayName();
		}
		return Core.API.getServer().getOfflinePlayer(playerId).getName();
	}

	public static ParticleEntry getParticleByName(String name) {
		return Core.API.getParticles().stream().filter(particles -> particles.getName().equals(name)).findFirst().orElse(null);
	}

	public static ParticleEntry getParticleBySection(String section) {
		return Core.API.getParticles().stream().filter(particles -> particles.getSection().equals(section)).findFirst().orElse(null);
	}

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
	
	public static void checkNameMCLikeAsync(Core main, UUID uuid) {
	    final String uri = "https://api.namemc.com/server/" + main.getNamemcURL() + "/likes?profile=" + uuid;
		AtomicReference<String> responseString = new AtomicReference<>("false");
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
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
				if (response.toString().equalsIgnoreCase("true")) {
					responseString.set("true");
				} else {
					responseString.set("false");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		future.whenCompleteAsync((t, u) -> {
			final boolean bool = Boolean.parseBoolean(responseString.get());
			Core.API.getManagerHandler().getProfileManager().getProfiles().get(uuid).setLikeNameMC(bool);
			Bukkit.getPlayer(uuid).sendMessage(bool ? Core.API.getLoaderHandler().getMessage().getNameMCLike() : Core.API.getLoaderHandler().getMessage().getNameMCUnlike());
		});
	}

	public static CompletableFuture<String> getPlayerPublicIP(Player player) {
		CompletableFuture<String> future = new CompletableFuture<>();
		try {
			URL url = new URL("https://api.ipify.org?format=json");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			player.getServer().getScheduler().runTaskAsynchronously(Core.API, () -> {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
					StringBuilder content = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					JSONObject jsonObject = new JSONObject(content.toString());
					String publicIP = jsonObject.getString("ip");
					future.complete(publicIP);
				} catch (IOException e) {
					future.completeExceptionally(e);
				}
			});
		} catch (IOException e) {
			future.completeExceptionally(e);
		}
		return future;
	}


	public static void disguise(final Player target, final Player disguised, DisguiseEntry disguiseEntry) {
		disguised.setFakeNameAndSkin(target, disguiseEntry.getName(), new Skin(disguiseEntry.getDataSkin(), disguiseEntry.getSignature()));
	}
}
