package net.redstoneore.legacyfactions.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;

/**
 * Represents a scoreboard for a player
 *
 */
public class FScoreboard {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	protected FScoreboard(FPlayer fplayer) {
		this.fplayer = fplayer;

		if (!FScoreboards.isSupportedByServer()) {
			this.scoreboard = null;
			this.bufferedObjective = null;
			return;
		}
		
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.bufferedObjective = new BufferedObjective(scoreboard);

		this.getPlayer().setScoreboard(scoreboard);
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private final Scoreboard scoreboard;
	private final FPlayer fplayer;
	private final BufferedObjective bufferedObjective;
	private FSidebarProvider defaultProvider;
	private FSidebarProvider temporaryProvider;
	protected Boolean removed = false;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Return the FPlayer for this class.
	 * @return FPlayer
	 */
	public FPlayer getFPlayer() {
		return this.fplayer;
	}

	/**
	 * Return the Player for this class.
	 * @return Player
	 */
	public Player getPlayer() {
		return this.fplayer.getPlayer();
	}

	
	/**
	 * Return the Scoreboard for this class
	 * @return Scoreboard
	 */
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
	
	/**
	 * Set the sidebar visibility 
	 * @param visible True if visible.
	 */
	public void setSidebarVisibility(boolean visible) {
		if (!FScoreboards.isSupportedByServer()) return;

		this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
	}
	
	/**
	 * Set the default scoreboard 
	 * @param provider FSidebarProvider to set
	 * @param updateIntervalSecs Time in seconds of how frequency we update.
	 */
	public void setDefaultSidebar(final FSidebarProvider provider, int updateIntervalSecs) {
		if (!FScoreboards.isSupportedByServer()) return;
		
		this.defaultProvider = provider;
		
		if (this.temporaryProvider == null) {
			// We have no temporary provider so update the BufferedObjective.
			this.updateObjective();
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (removed || provider != defaultProvider) {
					this.cancel();
					return;
				}
				
				if (temporaryProvider == null) {
					updateObjective();
				}
			}
		}.runTaskTimer(Factions.get(), updateIntervalSecs * 20, updateIntervalSecs * 20);
	}

	public void setTemporarySidebar(final FSidebarProvider provider) {
		if (!FScoreboards.isSupportedByServer()) return;
		
		this.temporaryProvider = provider;
		this.updateObjective();

		long scoreboardExpiresSecs = Config.scoreboardExpiresSecs;
		
		// Maybe they didn't want the scoreboard to expire 
		if (Config.scoreboardExpiresSecs <= 0) return;
		
		Bukkit.getScheduler().runTaskLater(Factions.get(), () -> {
			if (this.removed) return;
			if (this.temporaryProvider != provider) return;
			
			this.temporaryProvider = null;
			
			this.updateObjective();
		},  scoreboardExpiresSecs * 20);
	}

	private void updateObjective() {
		FSidebarProvider provider = this.temporaryProvider != null ? this.temporaryProvider : this.defaultProvider;

		if (provider == null) {
			this.bufferedObjective.hide();
		} else {
			this.bufferedObjective.setTitle(provider.getTitle(fplayer));
			this.bufferedObjective.setAllLines(provider.getLines(fplayer));
			this.bufferedObjective.flip();
		}
	}
	
}
