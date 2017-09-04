package net.redstoneore.legacyfactions.placeholder;

import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;

public abstract class FactionsPlaceholderFaction extends FactionsPlaceholder {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public FactionsPlaceholderFaction(String placeholder) {
		super(placeholder);
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public String get(Player player) {
		if (player == null) return null;
		FPlayer fplayer = FPlayerColl.get(player);
		if (fplayer == null) return null;
		return this.get(FPlayerColl.get(player).getFaction());
	}
	
	public abstract String get(Faction faction);

}
