package net.redstoneore.legacyfactions.expansion;

import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.expansion.fly.FactionsFly;

public class Expansions {

	/**
	 * Sync internal expansions, this doesn't apply to external expansions.
	 */
	public static void sync() {
		syncFactionsFly();
	}
	
	private static void syncFactionsFly() {
		if (Conf.factionsFlyExpansionEnabled) {
			if (!FactionsFly.get().isEnabled()) {
				FactionsFly.get().enable();
			}
		} else {
			if (FactionsFly.get().isEnabled()) {
				FactionsFly.get().disable();
			}
		}
	}
	
}
