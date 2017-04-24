package net.redstoneore.legacyfactions.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

import java.util.HashMap;
import java.util.Map;

public class FScoreboard {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
    private static final Map<FPlayer, FScoreboard> fscoreboards = new HashMap<FPlayer, FScoreboard>();

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //

    private static void init(FPlayer fplayer) {
        FScoreboard fboard = new FScoreboard(fplayer);
        fscoreboards.put(fplayer, fboard);

        if (fplayer.hasFaction()) {
            FTeamWrapper.applyUpdates(fplayer.getFaction());
        }
        FTeamWrapper.track(fboard);
    }

    public static void remove(FPlayer fplayer) {
        FScoreboard fboard = fscoreboards.remove(fplayer);

        if (fboard != null) {
            fboard.removed = true;
            FTeamWrapper.untrack(fboard);
        }
    }

    public static FScoreboard get(FPlayer fplayer) {
    	if (!fscoreboards.containsKey(fplayer)) {
    		init(fplayer);
    	}
        return fscoreboards.get(fplayer);
    }

    public static FScoreboard get(Player player) {
        return get(FPlayerColl.get(player));
    }
    
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
    
    private FScoreboard(FPlayer fplayer) {
        this.fplayer = fplayer;

        if (isSupportedByServer()) {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.bufferedObjective = new BufferedObjective(scoreboard);

            fplayer.getPlayer().setScoreboard(scoreboard);
        } else {
            this.scoreboard = null;
            this.bufferedObjective = null;
        }
    }
    
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

    private final Scoreboard scoreboard;
    private final FPlayer fplayer;
    private final BufferedObjective bufferedObjective;
    private FSidebarProvider defaultProvider;
    private FSidebarProvider temporaryProvider;
    private Boolean removed = false;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
    
    public static boolean isSupportedByServer() {
    	try {
    		return Bukkit.getScoreboardManager() != null;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
    protected FPlayer getFPlayer() {
        return this.fplayer;
    }

    protected Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setSidebarVisibility(Boolean visible) {
        if (!isSupportedByServer()) return;

        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }

    public void setDefaultSidebar(final FSidebarProvider provider, int updateInterval) {
        if (!isSupportedByServer()) return;

        this.defaultProvider = provider;
        if (this.temporaryProvider == null) {
            // We have no temporary provider; update the BufferedObjective!
            this.updateObjective();
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed || provider != defaultProvider) {
                    cancel();
                    return;
                }

                if (temporaryProvider == null) {
                    updateObjective();
                }
            }
        }.runTaskTimer(Factions.get(), updateInterval, updateInterval);
    }

    public void setTemporarySidebar(final FSidebarProvider provider) {
        if (!isSupportedByServer()) return;
        
        this.temporaryProvider = provider;
        this.updateObjective();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed) return;
                
                if (temporaryProvider == provider) {
                    temporaryProvider = null;
                    updateObjective();
                }
            }
        }.runTaskLater(Factions.get(), Conf.scoreboardExpires * 20);
    }

    private void updateObjective() {
        FSidebarProvider provider = temporaryProvider != null ? temporaryProvider : defaultProvider;

        if (provider == null) {
            bufferedObjective.hide();
        } else {
            bufferedObjective.setTitle(provider.getTitle(fplayer));
            bufferedObjective.setAllLines(provider.getLines(fplayer));
            bufferedObjective.flip();
        }
    }
    
}
