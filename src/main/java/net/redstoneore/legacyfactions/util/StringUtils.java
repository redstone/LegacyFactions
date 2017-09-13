package net.redstoneore.legacyfactions.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
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
