package net.redstoneore.legacyfactions.placeholder;

import org.bukkit.entity.Player;

public abstract class FactionsPlaceholderRelation extends FactionsPlaceholder {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public FactionsPlaceholderRelation(String placeholder) {
		super(placeholder);
	}

	// -------------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------------- //
	
	public abstract String get(Player one, Player two);

}
