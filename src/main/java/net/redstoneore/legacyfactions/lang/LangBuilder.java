package net.redstoneore.legacyfactions.lang;

import org.bukkit.command.CommandSender;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.util.TextUtil;

public class LangBuilder {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public LangBuilder(Lang lang) {
		this.message = lang.toString();
	}
	
	public LangBuilder(String message) {
		this.message = message;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private String message;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Replace a string with another string.
	 * @param find
	 * @param replace
	 * @return
	 */
	public LangBuilder replace(String find, Object replace) {
		this.message = this.message.replace(find, replace.toString());
		return this;
	}
	
	@Override
	public String toString() {
		return this.message;
	}
	
	/**
	 * Parse tags and colours on this lang.
	 * @return
	 */
	public LangBuilder parse() {
		this.message = TextUtil.get().parse(this.message);
		return this;
	}
	
	/**
	 * Send to a FPlayer
	 * @param fplayer
	 * @return
	 */
	public LangBuilder sendTo(FPlayer fplayer) {
		fplayer.sendMessage(this.message);
		return this;
	}
	
	/**
	 * Send this message parsed to a FPlayer. It iwll not parse the string in this class.
	 * @param fplayer
	 * @return
	 */
	public LangBuilder sendToParsed(FPlayer fplayer) {
		fplayer.sendMessage(TextUtil.get().parse(this.message));
		return this;
	}
	
	/**
	 * Send to a faction.
	 * @param faction
	 * @return
	 */
	public LangBuilder sendTo(Faction faction) {
		faction.sendMessage(this.message);
		return this;
	}
	
	/**
	 * Send this message parsed to a faction. It will not parse the string in this class.
	 * @param faction
	 * @return
	 */
	public LangBuilder sendToParsed(Faction faction) {
		faction.sendMessage(TextUtil.get().parse(this.message));
		return this;
	}
	
	/**
	 * Send to a command sender.
	 * @param sender
	 * @return
	 */
	public LangBuilder sendTo(CommandSender sender) {
		sender.sendMessage(this.message);
		return this;
	}
	
	/**
	 * Send this message parsed to a command sender. It will not parse the string in this class.
	 * @param sender
	 * @return
	 */
	public LangBuilder sendToParsed(CommandSender sender) {
		sender.sendMessage(TextUtil.get().parse(this.message));
		return this;
	}
	
}
