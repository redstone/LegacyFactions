package net.redstoneore.legacyfactions.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.lang.Lang;

import java.util.*;

public class FTeamWrapper {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

    private static final Map<Faction, FTeamWrapper> wrappers = new HashMap<>();
    private static final List<FScoreboard> tracking = new ArrayList<>();
    private static int factionTeamPtr;
    private static final Set<Faction> updating = new HashSet<>();

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //

    public static void applyUpdatesLater(final Faction faction) {
        if (!FScoreboards.isSupportedByServer() || faction.isWilderness() || !Config.scoreboardDefaultPrefixes || !updating.add(faction)) return;
        
        Bukkit.getScheduler().runTask(Factions.get(), () -> {
            updating.remove(faction);
            applyUpdates(faction);
        });
    }

    public static void applyUpdates(Faction faction) {
        if (!FScoreboards.isSupportedByServer() || faction.isWilderness() || !Config.scoreboardDefaultPrefixes) return;
        
        // Make sure we're not already updating
        if (updating.contains(faction)) return;

        FTeamWrapper wrapper = wrappers.get(faction);
        Set<FPlayer> factionMembers = faction.getMembers();

        if (wrapper != null && FactionColl.get().getFactionById(faction.getId()) == null) {
            // Faction was disbanded
            wrapper.unregister();
            wrappers.remove(faction);
            return;
        }

        if (wrapper == null) {
            wrapper = new FTeamWrapper(faction);
            wrappers.put(faction, wrapper);
        }
        
        for (OfflinePlayer player : wrapper.getPlayers()) {
            if (!player.isOnline() || !factionMembers.contains(FPlayerColl.get(player))) {
                // Player is offline or no longer in faction
                wrapper.removePlayer(player);
            }
        }

        for (FPlayer fmember : factionMembers) {
            if (!fmember.isOnline()) continue;
            
            // Scoreboard might not have player; add him/her
            wrapper.addPlayer(fmember.getPlayer());
        }

        wrapper.updatePrefixes();
    }

    public static void updatePrefixes(Faction faction) {
        if (!FScoreboards.isSupportedByServer()) return;
        
        if (!wrappers.containsKey(faction)) {
            applyUpdates(faction);
        } else {
            wrappers.get(faction).updatePrefixes();
        }
    }

    protected static void track(FScoreboard scoreboard) {
        if (!FScoreboards.isSupportedByServer()) return;
        
        tracking.add(scoreboard);
        
        wrappers.values().forEach(wrapper -> {
        	wrapper.add(scoreboard);
        });
    }

    protected static void untrack(FScoreboard scoreboard) {
        if (!FScoreboards.isSupportedByServer()) return;
        
        tracking.remove(scoreboard);

        wrappers.values().forEach(wrapper -> {
        	wrapper.remove(scoreboard);
        });
    }
    
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

    private FTeamWrapper(Faction faction) {
        this.teamName = "faction_" + (factionTeamPtr++);
        this.faction = faction;
        
        tracking.forEach(scoreboard -> {
        	add(scoreboard);
        });
    }
    
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

    private final Map<FScoreboard, Team> teams = new HashMap<>();
    private final String teamName;
    private final Faction faction;
    private final Set<OfflinePlayer> members = new HashSet<>();

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

    @SuppressWarnings("deprecation")
	private void add(FScoreboard scoreboard) {
        Scoreboard board = scoreboard.getScoreboard();
        Team team = board.registerNewTeam(this.teamName);
        this.teams.put(scoreboard, team);

        for (OfflinePlayer player : this.getPlayers()) {
            team.addPlayer(player);
        }

        this.updatePrefix(scoreboard);
    }

    private void remove(FScoreboard scoreboard) {
        this.teams.remove(scoreboard).unregister();
    }

    private void updatePrefixes() {
        if (!Config.scoreboardDefaultPrefixes) return;
        
        this.teams.keySet().forEach(scoreboard -> {
        	updatePrefix(scoreboard);
        });
    }

    private void updatePrefix(FScoreboard scoreboard) {
        if (!Config.scoreboardDefaultPrefixes) return;
        
        FPlayer fplayer = scoreboard.getFPlayer();
        Team team = this.teams.get(scoreboard);

        String prefix = Lang.DEFAULT_PREFIX.toString();
        prefix = prefix.replace("{relationcolor}", faction.getRelationTo(fplayer).getColor().toString());
        prefix = prefix.replace("{faction}", faction.getTag().substring(0, Math.min("{faction}".length() + 16 - prefix.length(), this.faction.getTag().length())));
        
        if (team.getPrefix() != null && team.getPrefix().equals(prefix)) return;
        
        team.setPrefix(prefix);
    }

    @SuppressWarnings("deprecation")
	private void addPlayer(OfflinePlayer player) {
        if (!this.members.add(player)) return;
        
        this.teams.values().forEach(team -> {
            team.addPlayer(player);
        });
    }

    @SuppressWarnings("deprecation")
	private void removePlayer(OfflinePlayer player) {
        if (!this.members.remove(player)) return;
        this.teams.values().forEach(team -> {
        	team.removePlayer(player);
        });
    }

    private Set<OfflinePlayer> getPlayers() {
        return new HashSet<>(this.members);
    }

    private void unregister() {
    	this.teams.values().forEach(team -> {
    		team.unregister();
    	});
        
        this.teams.clear();
    }
    
}
