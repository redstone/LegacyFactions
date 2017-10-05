package net.redstoneore.legacyfactions.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.google.gson.JsonParser;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.lang.Lang;

import java.lang.Character.UnicodeScript;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MiscUtil {

	/**
	 * Streams over all faction and checks if the emblem is in use
	 * @param emblem
	 * @return
	 */
	public static boolean isEmblemTaken(String emblem) {
		try {
			return FactionColl.all().stream()
				.filter(faction -> faction.getEmblem().equalsIgnoreCase(emblem))
				.findFirst()
				.isPresent();
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static EntityType creatureTypeFromEntity(Entity entity) {
		if (!(entity instanceof Creature)) {
			return null;
		}

		String name = entity.getClass().getSimpleName();
		name = name.substring(5); // Remove "Craft"

		try {
			return EntityType.valueOf(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static boolean isValidJSON(String json) {
		try {
			new JsonParser().parse(json);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isValidJSON(String json, Class<?> clazz) {
		try {
			Factions.get().getGson().fromJson(json, clazz);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	// Inclusive range
	public static long[] range(long start, long end) {
		long[] values = new long[(int) Math.abs(end - start) + 1];

		if (end < start) {
			long oldstart = start;
			start = end;
			end = oldstart;
		}

		for (long i = start; i <= end; i++) {
			values[(int) (i - start)] = i;
		}

		return values;
	}

	/// TODO create tag whitelist!!
	private static HashSet<String> englishCharacters = new HashSet<>(Arrays.asList(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"}));

	
	public static String getComparisonString(String str) {
		if (str == null) return null;
		
		String ret = "";

		str = ChatColor.stripColor(str);
		str = str.toLowerCase();

		for (char c : str.toCharArray()) {
			if (englishCharacters.contains(String.valueOf(c))) {
				ret += c;
			} else {
				for (Entry<UnicodeScript, Boolean> entry : Config.enabledScriptSupport.entrySet()) {
					if (entry.getValue() == true && entry.getKey() != null && isUnicodeScript(entry.getKey(), String.valueOf(c))) {
						ret += c;
					}
				}
			}
		}
		return ret.toLowerCase();
	}

	public static ArrayList<String> validateTag(String str) {
		ArrayList<String> errors = new ArrayList<>();

		if (getComparisonString(str).length() < Config.factionTagLengthMin) {
			errors.add(TextUtil.get().parse(Lang.GENERIC_FACTIONTAG_TOOSHORT.toString(), Config.factionTagLengthMin));
		}

		if (str.length() > Config.factionTagLengthMax) {
			errors.add(TextUtil.get().parse(Lang.GENERIC_FACTIONTAG_TOOLONG.toString(), Config.factionTagLengthMax));
		}

		for (char c : str.toCharArray()) {
			if (!englishCharacters.contains(String.valueOf(c))) {
				Config.enabledScriptSupport.forEach( (script, enabled) -> {
					if (!enabled) return;
					
					if (isUnicodeScript(script, String.valueOf(c))) return;
					errors.add(TextUtil.get().parse(Lang.GENERIC_FACTIONTAG_ALPHANUMERIC.toString(), c));
				});
			}
		}

		return errors;
	}
	
	public static boolean isUnicodeScript(Character.UnicodeScript script, String value) {
		return value.codePoints().anyMatch(
				codepoint ->
				Character.UnicodeScript.of(codepoint) == script);
	}


	public static Iterable<FPlayer> rankOrder(Iterable<FPlayer> players) {
		List<FPlayer> admins = new ArrayList<>();
		List<FPlayer> coleaders = new ArrayList<>();
		List<FPlayer> moderators = new ArrayList<>();
		List<FPlayer> normal = new ArrayList<>();

		for (FPlayer player : players) {
			switch (player.getRole()) {
				case ADMIN:
					admins.add(player);
					break;

				case COLEADER:
					coleaders.add(player);
					break;

				case MODERATOR:
					moderators.add(player);
					break;

				case NORMAL:
					normal.add(player);
					break;
			}
		}

		List<FPlayer> ret = new ArrayList<>();
		ret.addAll(admins);
		ret.addAll(coleaders);
		ret.addAll(moderators);
		ret.addAll(normal);
		return ret;
	}
	
	public static LinkedHashSet<String> linkedHashSet(String... items) {
		LinkedHashSet<String> set = new LinkedHashSet<>();

		for (String item : items) {
			set.add(item);
		}
		
		return set;
	}
	
	public static Map<Relation, Integer> map(Relation a, Integer b, Object... extras) {
		Map<Relation, Integer> map = new HashMap<>();
		map.put(a, b);
		
		Relation key = null;
		
		for (Object o : extras) {
			if (key == null) {
				key = (Relation) o;
			} else {
				map.put(key, (Integer) o);
				key = null;
			}
		}
		
		return map;
	}
	
	public static Map<String, Double> map(String a, Double b, Object... extras) {
		Map<String, Double> map = new HashMap<>();
		map.put(a, b);
		
		String key = null;
		
		for(Object o : extras) {
			if (key == null) {
				key = (String) o;
			} else {
				map.put(key, (Double) o);
				key = null;
			}
		}
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, List<String>> map(String a, List<String> b, Object... extras) {
		 Map<String, List<String>> map = new HashMap<>();
		 map.put(a, b);
		 
	 	String key = null;
		
	 	for(Object o : extras) {
	 		if (key == null) {
	 			key = (String) o;
	 		} else {
	 			map.put(key, (List<String>) o);
	 			key = null;
	 		}
	 	}
	 	
	 	return map;

	}

	public static Map<UnicodeScript, Boolean> map(UnicodeScript a, boolean b, Object... extras) {
		Map<UnicodeScript, Boolean> map = new HashMap<>();
		map.put(a, b);
		
		UnicodeScript key = null;
		
		for(Object o : extras) {
			if (key == null) {
				key = (UnicodeScript) o;
			} else {
				map.put(key, (Boolean) o);
				key = null;
			}
		}
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static <A, B>Map<A, B> newMap(A firstItem, B secondItem, Object... moreItems) {
		Map<A, B> newMap = new HashMap<>();
		
		newMap.put(firstItem, secondItem);
		
		A key = null;
		
		for (Object item : moreItems) {
			if (key == null) {
				key = (A) item;
				continue;
			}
			
			B value = (B) item;
			newMap.put(key, value); 
			
			// reset
			key = null;
		}
 		return newMap;
	}
	
	@SafeVarargs
	public static <T> T firstNotNull(T... items) {
		for (T item : items) {
			if (item != null) {
				return item;
			}
		}
		return null;
	}
	
	public static boolean classExists(String name) {
		try {
			Class.forName(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	
}
