package net.redstoneore.legacyfactions.entity.persist.shared;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;

public abstract class SharedFactionColl extends FactionColl {

	// -------------------------------------------------- //
	// TAGS
	// -------------------------------------------------- //
	
	@Override
    public boolean isTagTaken(String str) {
        return this.getByTag(str) != null;
    }
	
	@Override
	public Set<String> getFactionTags() {
		return this.getAllFactions().stream()
			.map(Faction::getTag)
			.collect(Collectors.toSet());
	}
	
	@Override
	public Faction getBestTagMatch(String start) {
		int best = 0;
		start = start.toLowerCase();
		int minlength = start.length();
		Faction bestMatch = null;
		
		// TODO: filter this better, this is slow
		for (Faction faction : this.getAllFactions()) {
			String candidate = faction.getTag();
			candidate = ChatColor.stripColor(candidate);
			if (candidate.length() < minlength) {
				continue;
			}
			if (!candidate.toLowerCase().startsWith(start)) {
				continue;
			}

			// The closer to zero the better
			int lendiff = candidate.length() - minlength;
			if (lendiff == 0) {
				return faction;
			}
			if (lendiff < best || best == 0) {
				best = lendiff;
				bestMatch = faction;
			}
		}

		return bestMatch;
	}
	
	public void validate() {
		FPlayerColl.all(fplayer -> {
			Faction faction = fplayer.getFaction();
			if (faction == null) {
				Factions.get().log("Invalid faction id on " + fplayer.getName() + ":" + fplayer.getFactionId());
				fplayer.resetFactionData();
				return;
			}
			faction.memberAdd(fplayer);
		});
	}
	
	// -------------------------------------------------- //
	// SAVE
	// -------------------------------------------------- //
	
	@Override
	public void forceSave() {
		forceSave(true);
	}
	
	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
	
    public abstract Faction generateFactionObject(String string);

}
