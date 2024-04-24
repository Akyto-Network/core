package gym.core.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.bukkit.ChatColor;

public class Utils {
	
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
}
