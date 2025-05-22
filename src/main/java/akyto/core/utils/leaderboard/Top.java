package akyto.core.utils.leaderboard;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Top {

    private int elo_id;
    private final Map<String, Integer> topBoard = new HashMap<>();
    private final ArrayList<String> lore = new ArrayList<>();

    public Top(int elo_id, Map<String, int[]> map) {
    	this.elo_id = elo_id;
        extirpate(map);
        organise();
    }

    public Top(Map<String, int[]> map) {
        map.forEach((key, value) -> topBoard.put(key, (int) Arrays.stream(value).average().orElse(0)));
        organise();
    }

    private void extirpate(Map<String, int[]> map) {
        map.forEach((key, value) -> topBoard.put(key, value[elo_id]));
    }

    private void organise() {
        List<Map.Entry<String, Integer>> entries = topBoard.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        int x = 1;
        for (Map.Entry<String, Integer> entry : entries) {
            if (x <= 10) {
                String hashtag;
                if (x < 4) {
                    hashtag = (x == 1 ? ChatColor.RED.toString() : x == 2 ? ChatColor.YELLOW.toString() : ChatColor.GOLD.toString()) + ChatColor.MAGIC + "#" + ChatColor.RESET + " ";
                }
                else {
                    hashtag = ChatColor.GRAY.toString() + ChatColor.ITALIC + "#";
                }
            	lore.add(
                        hashtag + ChatColor.DARK_RED + ChatColor.BOLD + ChatColor.ITALIC + x + ChatColor.GRAY + " ৷ " + ChatColor.RED + entry.getKey() + ChatColor.GRAY + " ⇒ " + ChatColor.WHITE + entry.getValue());
            }
        	x++;
        }
    }
}