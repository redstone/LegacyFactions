package net.redstoneore.legacyfactions.util;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.entity.persist.Persist;
import net.redstoneore.legacyfactions.lang.Lang;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	public static Map<String, String> RAW_TAGS = MiscUtil.newMap(
		"l", "<green>",
		"a", "<gold>",
		"b", "<silver>",
		"i", "<yellow>",
		"g", "<lime>",
		"b", "<rose>",
		"h", "<pink>",
		"c", "<aqua>",
		"p", "<teal>"
	);
	
	public static final transient Pattern patternTag = Pattern.compile("<([a-zA-Z0-9_]*)>");
	
	private final static String titleizeLine = repeat("_", 50);
	private final static int titleizeBalance = -1;
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static TextUtil instance = null;
	private TextUtil() { }
	public static TextUtil get() {
		if (instance == null) {
			TextUtil textUtil = new TextUtil();

			Type type = new TypeToken<Map<String, String>>() { }.getType();
			
			Map<String, String> tagsFromFile = null;
			try { 
				tagsFromFile = Persist.get().load(type, "tags");
			} catch (Exception e) {
				// Fail silently
			}
			
			if (tagsFromFile != null) {
				RAW_TAGS.putAll(tagsFromFile);
			}
			Persist.get().save(RAW_TAGS, "tags");
			
			RAW_TAGS.entrySet().forEach(rawTag -> {
				textUtil.tags.put(rawTag.getKey(), TextUtil.parseColor(rawTag.getValue()));
			});
			
			instance = textUtil;
		}
		return instance;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public Map<String, String> tags = new HashMap<>();
	
	// -------------------------------------------- //
	// PLACEHOLDER UTIL
	// -------------------------------------------- //
	
	public String replacePlaceholders(String text, Player player) {
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
		}
		return text;
	}
	
	public String replacePlaceholders(String text, Player player1, Player player2) {
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			text = me.clip.placeholderapi.PlaceholderAPI.setRelationalPlaceholders(player1, player2, text);
		}
		return text;
	}
	
	
	// -------------------------------------------- //
	// Top-level parsing functions.
	// -------------------------------------------- //

	public String parse(String str, Object... args) {
		return String.format(this.parse(str), args);
	}

	public String parse(String str) {
		return this.parseTags(parseColor(str));
	}

	// -------------------------------------------- //
	// Tag parsing
	// -------------------------------------------- //

	public String parseTags(String str) {
		return replaceTags(str, this.tags);
	}
	
	public static String replaceTags(String str, Map<String, String> tags) {
		StringBuffer ret = new StringBuffer();
		Matcher matcher = patternTag.matcher(str);
		while (matcher.find()) {
			String tag = matcher.group(1);
			String repl = tags.get(tag);
			if (repl == null) {
				matcher.appendReplacement(ret, "<" + tag + ">");
			} else {
				matcher.appendReplacement(ret, repl);
			}
		}
		matcher.appendTail(ret);
		return ret.toString();
	}

	// -------------------------------------------- //
	// Fancy parsing
	// -------------------------------------------- //

	public FancyMessage parseFancy(String prefix) {
		return toFancy(parse(prefix));
	}

	public static FancyMessage toFancy(String first) {
		String text = "";
		FancyMessage message = new FancyMessage(text);
		ChatColor color = null;
		char[] chars = first.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '§') {
				if (color != null) {
					if (color.isColor()) {
						message.then(text).color(color);
					} else {
						message.then(text).style(color);
					}
					text = "";
					color = ChatColor.getByChar(chars[i + 1]);
				} else {
					color = ChatColor.getByChar(chars[i + 1]);
				}
				i++; // skip color char
			} else {
				text += chars[i];
			}
		}
		if (text.length() > 0) {
			if (color != null) {
				if (color.isColor()) {
					message.then(text).color(color);
				} else {
					message.then(text).style(color);
				}
			} else {
				message.text(text);
			}
		}
		return message;
	}

	// -------------------------------------------- //
	// COLOUR PARSING
	// -------------------------------------------- //

	public static String parseColor(String string) {
		string = parseColorAmp(string);
		string = parseColorAcc(string);
		string = parseColorTags(string);
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String parseColorAmp(String string) {
		string = string.replaceAll("(§([a-z0-9]))", "\u00A7$2");
		string = string.replaceAll("(&([a-z0-9]))", "\u00A7$2");
		string = string.replace("&&", "&");
		return string;
	}

	public static String parseColorAcc(String string) {
		return string.replace("`e", "").replace("`r", ChatColor.RED.toString()).replace("`R", ChatColor.DARK_RED.toString()).replace("`y", ChatColor.YELLOW.toString()).replace("`Y", ChatColor.GOLD.toString()).replace("`g", ChatColor.GREEN.toString()).replace("`G", ChatColor.DARK_GREEN.toString()).replace("`a", ChatColor.AQUA.toString()).replace("`A", ChatColor.DARK_AQUA.toString()).replace("`b", ChatColor.BLUE.toString()).replace("`B", ChatColor.DARK_BLUE.toString()).replace("`p", ChatColor.LIGHT_PURPLE.toString()).replace("`P", ChatColor.DARK_PURPLE.toString()).replace("`k", ChatColor.BLACK.toString()).replace("`s", ChatColor.GRAY.toString()).replace("`S", ChatColor.DARK_GRAY.toString()).replace("`w", ChatColor.WHITE.toString());
	}

	public static String parseColorTags(String string) {
		return string.replace("<empty>", "").replace("<black>", "\u00A70").replace("<navy>", "\u00A71").replace("<green>", "\u00A72").replace("<teal>", "\u00A73").replace("<red>", "\u00A74").replace("<purple>", "\u00A75").replace("<gold>", "\u00A76").replace("<silver>", "\u00A77").replace("<gray>", "\u00A78").replace("<blue>", "\u00A79").replace("<lime>", "\u00A7a").replace("<aqua>", "\u00A7b").replace("<rose>", "\u00A7c").replace("<pink>", "\u00A7d").replace("<yellow>", "\u00A7e").replace("<white>", "\u00A7f");
	}

	// -------------------------------------------------- //
	// STANDARD UTILS
	// -------------------------------------------------- //

	public static String upperCaseFirst(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public static String implode(List<String> list, String glue) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				ret.append(glue);
			}
			ret.append(list.get(i));
		}
		return ret.toString();
	}

	public static String repeat(String s, int times) {
		if (times <= 0) {
			return "";
		} else {
			return s + repeat(s, times - 1);
		}
	}

	// -------------------------------------------------- //
	// MATERIAL NAME UTILS
	// -------------------------------------------------- //

	public static String getMaterialName(Material material) {
		return material.toString().replace('_', ' ').toLowerCase();
	}

	// -------------------------------------------- //
	// PAGING AND TITLES
	// -------------------------------------------- //
	
	public String titleize(String str) {
		String center = ".[ " + parseTags("<l>") + str + parseTags("<a>") + " ].";
		int centerlen = ChatColor.stripColor(center).length();
		int pivot = titleizeLine.length() / 2;
		int eatLeft = (centerlen / 2) - titleizeBalance;
		int eatRight = (centerlen - eatLeft) + titleizeBalance;

		if (eatLeft < pivot) {
			return parseTags("<a>") + titleizeLine.substring(0, pivot - eatLeft) + center + titleizeLine.substring(pivot + eatRight);
		} else {
			return parseTags("<a>") + center;
		}
	}

	public ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title) {
		ArrayList<String> page = new ArrayList<>();
		int pageZeroBased = pageHumanBased - 1;
		int pageheight = 9;
		int pagecount = (lines.size() / pageheight) + 1;

		page.add(this.titleize(title + " " + pageHumanBased + "/" + pagecount));

		if (pagecount == 0) {
			page.add(this.parseTags(Lang.NOPAGES.toString()));
			return page;
		} else if (pageZeroBased < 0 || pageHumanBased > pagecount) {
			page.add(this.parseTags(Lang.INVALIDPAGE.format(pagecount)));
			return page;
		}

		int from = pageZeroBased * pageheight;
		int to = from + pageheight;
		if (to > lines.size()) {
			to = lines.size();
		}

		page.addAll(lines.subList(from, to));

		return page;
	}

	public static String getBestStartWithCI(Collection<String> candidates, String start) {
		String ret = null;
		int best = 0;

		start = start.toLowerCase();
		int minlength = start.length();
		for (String candidate : candidates) {
			if (candidate.length() < minlength) {
				continue;
			}
			if (!candidate.toLowerCase().startsWith(start)) {
				continue;
			}

			// The closer to zero the better
			int lendiff = candidate.length() - minlength;
			if (lendiff == 0) {
				return candidate;
			}
			if (lendiff < best || best == 0) {
				best = lendiff;
				ret = candidate;
			}
		}
		return ret;
	}
	
}
