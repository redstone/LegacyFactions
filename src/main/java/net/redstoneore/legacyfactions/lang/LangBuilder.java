package net.redstoneore.legacyfactions.lang;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.util.TextUtil;

public class LangBuilder {

	public LangBuilder(String message) {
		this.message = message;
	}
	
	private String message;
	
	public LangBuilder replace(String find, Object replace) {
		this.message = this.message.replace(find, replace.toString());
		return this;
	}
	
	public String toString() {
		return this.message;
	}
	
	public LangBuilder parse() {
		this.message = TextUtil.get().parse(this.message);
		return this;
	}
	
	public LangBuilder sendTo(FPlayer fplayer) {
		fplayer.sendMessage(this.message);
		return this;
	}
	
	public LangBuilder sendToParsed(FPlayer fplayer) {
		fplayer.sendMessage(TextUtil.get().parse(this.message));
		return this;
	}
	
	public LangBuilder sendTo(Faction faction) {
		faction.sendMessage(this.message);
		return this;
	}
	
	public LangBuilder sendToParsed(Faction faction) {
		faction.sendMessage(TextUtil.get().parse(this.message));
		return this;
	}
	
}
