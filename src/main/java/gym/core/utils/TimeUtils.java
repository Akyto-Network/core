package gym.core.utils;

import java.util.Calendar;
import java.util.TimerTask;

import kezukdev.akyto.duel.Duel;

public class TimeUtils {
	
	public TimeUtils(final String args, Calendar calendar) {
        if (args.contains("y")) {
        	final String year = args.replace("y", "");
        	calendar.add(Calendar.YEAR, Integer.parseInt(year));
        }
        if (args.contains("M")) {
        	final String minutes = args.replace("M", "");
        	calendar.add(Calendar.MINUTE, Integer.parseInt(minutes));
        }
        if (args.contains("d")) {
        	final String day = args.replace("d", "");
        	calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        }
        if (args.contains("h")) {
        	final String hours = args.replace("h", "");
        	calendar.add(Calendar.HOUR, Integer.parseInt(hours));
        }
        if (args.contains("min")) {
        	final String minutes = args.replace("min", "");
        	calendar.add(Calendar.MINUTE, Integer.parseInt(minutes));
        }
        if (args.contains("s")) {
        	final String second = args.replace("s", "");
        	calendar.add(Calendar.SECOND, Integer.parseInt(second));
        }
	}
	
    public static void startDuration(final Duel matchEntry) {
        matchEntry.getTimer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDuration(matchEntry);
            }
        }, 1000, 1000);
    }
    
    private static void updateDuration(final Duel matchEntry) {
        long currentTime = System.currentTimeMillis();
        matchEntry.duration = currentTime - matchEntry.getStartTime();
    }

}
