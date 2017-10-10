package net.redstoneore.legacyfactions.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of supported languages in LegacyFactions.
 */
public enum Langs {

	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- //
	
	EN_AU("en_au", "Australian English", "lang/en_AU.yml"),
	EN_GB("en_gb", "British English", "lang/en_GB.yml"),
	EN_US("en_us", "American English", "lang/en_US.yml"),
	JA_JP("ja_jp", "Japanese", "lang/ja_JP.yml"),
	NL_NL("nl_nl", "Dutch", "lang/nl_NL.yml"),
	PL_PL("pl_PL", "Polish", "lang/pl_PL.yml"),
	ZH_CN("zh_cn", "Chinese", "lang/zh_CN.yml"),
	
	;
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static List<String> all() {
		List<String> langs = new ArrayList<>();
		for (Langs lang : values() ) {
			langs.add(lang.name());
		}
		return langs;
	}
	
	// -------------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------------- //
	
	public static final Langs DEFAULT = Langs.EN_AU;
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	Langs(String shortname, String longname, String path) {
		this.shortname =  shortname;
		this.longname = longname;
		this.path = path;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private String shortname;
	private String longname;
	private String path;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get the short name for this language.
	 * @return Short name;
	 */
	public String getShortName() {
		return this.shortname;
	}
	
	/**
	 * get the long name for this language.
	 * @return Long name;
	 */
	public String getLongName() {
		return this.longname;
	}
	
	/**
	 * Get the path of the language file resource.
	 * @return The path to the language file.
	 */
	public String getPath() {
		return this.path;
	}
	
}
