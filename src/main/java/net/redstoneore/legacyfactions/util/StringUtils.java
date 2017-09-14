package net.redstoneore.legacyfactions.util;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.redstoneore.legacyfactions.entity.FPlayer;

public class StringUtils {
	
	public static String join(List<FPlayer> collection) {
		List<String> strings = collection.stream()
				.map(fplayer -> fplayer.getName())
				.collect(Collectors.toList());
		
		return join(strings
				, ",");
	}
	
	public static String join(Collection<String> collection, String seperator) {
		String separator = ", ";
		int total = collection.size() * separator.length();
		
		for (String s : collection) {
		    total += s.length();
		}

		StringBuilder sb = new StringBuilder(total);
		collection.forEach(string -> sb.append(seperator).append(string));
		
		return sb.toString();
	}
	
	public static String replaceWithFn(CharSequence input, String regexp, Replacement replacer, int group) {
		// adapter from https://stackoverflow.com/a/13932688/2376468
		Matcher matcher = Pattern.compile(regexp).matcher(input);
		StringBuffer sb = new StringBuffer();
		try {
			while (matcher.find()) {
				matcher.appendReplacement(sb, replacer.run(matcher.group(group)));
			}
			matcher.appendTail(sb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	@FunctionalInterface
	public static interface Replacement {
		
		public String run(String replace);
		
	}

}
