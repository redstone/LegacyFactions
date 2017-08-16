package net.redstoneore.legacyfactions.util.cross;

public class CrossColour implements Cross<CrossColour> {
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static CrossColour of(DefaultChatColour colour) {
		return new CrossColour(colour.name());
	}
	
	public static CrossColour of(String colour) {
		return new CrossColour(colour);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public CrossColour(String name) {
		this.name = name;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	protected String name;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public String getName() {
		return this.name;
	}
	
	public org.bukkit.ChatColor toColor() {
		return org.bukkit.ChatColor.valueOf(this.name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CrossColour)) return false;
		
		if (obj == this) return true;
		
		if (((CrossColour) obj).name == this.name) return true;
		
		return false;
	}
	
	@Override
	public boolean is(CrossColour what) {
		return this.equals(what);
	}
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- //
	
	public enum DefaultChatColour {
		BLACK,
		DARK_BLUE,
		DARK_GREEN,
		DARK_AQUA,
		DARK_RED,
		DARK_PURPLE,
		GOLD,
		GRAY,
		DARK_GRAY,
		BLUE,
		GREEN,
		AQUA,
		RED,
		LIGHT_PURPLE,
		YELLOW,
		WHITE,
		MAGIC,
		BOLD,
		STRIKETHROUGH,
		UNDERLINE,
		ITALIC,
		RESET,
		
		;
	}
	
}
