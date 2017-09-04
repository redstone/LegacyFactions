package net.redstoneore.legacyfactions.placeholder;

import org.bukkit.entity.Player;

public abstract class FactionsPlaceholder {

	public FactionsPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	
	private String placeholder = "";
	
	public String placeholder() {
		return this.placeholder;
	}
	
	public abstract String get(Player player);
	
	public void adapt(FactionsPlaceholdersAdapter adapter) {
		adapter.adapt(this);
	}
	
}
