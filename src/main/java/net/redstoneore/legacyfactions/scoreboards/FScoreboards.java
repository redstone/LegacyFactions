package net.redstoneore.legacyfactions.scoreboards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

public class FScoreboards {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final Map<FPlayer, FScoreboard> fscoreboards = new HashMap<FPlayer, FScoreboard>();

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //

	/**
	 * Performs a try catch to see if scoreboards are supported on this server
	 * @return true if supported
	 */
	public static boolean isSupportedByServer() {
		try {
			return Bukkit.getScoreboardManager() != null;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Remove a player from the scoreboard
	 * @param fplayer to remove
	 */
	public static void remove(FPlayer fplayer) {
		FScoreboard fboard = fscoreboards.remove(fplayer);

		if (fboard != null) {
			fboard.removed = true;
			FTeamWrapper.untrack(fboard);
		}
	}
	
	/**
	 * Returns the scoreboard a player
	 * @param fplayer to fetch for
	 * @return FScoreboard that belongs to the player
	 */
	public static FScoreboard get(FPlayer fplayer) {
		if (!fscoreboards.containsKey(fplayer)) {
			// Initiliase the scoreboard
			FScoreboard fscoreboard = new FScoreboard(fplayer);
			fscoreboards.put(fplayer, fscoreboard);
			
			// If they have a faction we apply the updates now
			if (fplayer.hasFaction()) {
				FTeamWrapper.applyUpdates(fplayer.getFaction());
			}
			FTeamWrapper.track(fscoreboard);
		}
		
		return fscoreboards.get(fplayer);
	}

	public static FScoreboard get(Player player) {
		return get(FPlayerColl.get(player));
	}
	
}
