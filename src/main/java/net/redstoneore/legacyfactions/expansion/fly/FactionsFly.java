package net.redstoneore.legacyfactions.expansion.fly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.expansion.FactionsExpansion;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;

public class FactionsFly extends FactionsExpansion {
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //

	public static boolean canFlyHere(FPlayer fplayer, Locality locality) {
		return canFlyHere(fplayer, locality, false);
	}
	
	public static boolean canFlyHere(FPlayer fplayer, Locality locality, boolean debugVerbose) {
		Faction factionAtLocation = Board.get().getFactionAt(locality);
		Relation relation = fplayer.getRelationTo(factionAtLocation);
		
		if (factionAtLocation.isWilderness()) {
			return fplayer.hasPermission("factions.fly.wilderness");
		}
		if (factionAtLocation.isWarZone()) {
			return fplayer.hasPermission("factions.fly.warzone");
		}
		if (factionAtLocation.isSafeZone()) {
			return fplayer.hasPermission("factions.fly.safezone");
		}
		
		switch (relation) {
		case ALLY:
			return fplayer.hasPermission("factions.fly.ally");
		case ENEMY:
			return fplayer.hasPermission("factions.fly.enemy");
		case MEMBER:
			return fplayer.hasPermission("factions.fly.member");
		case NEUTRAL:
			return fplayer.hasPermission("factions.fly.neutral");
		case TRUCE:
			return fplayer.hasPermission("factions.fly.truce");
		default:
			return false;
		}
	}
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsFly i = new FactionsFly();
	public static FactionsFly get() { return i; }
	private FactionsFly() { }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- // 
	
	private List<UUID> fallingPlayers = new ArrayList<>();
	
	private Collection<Listener> listeners = Lists.newArrayList(
		FactionsFlyListener.get(),
		FactionsFlyCommand.get()
	);
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public String getName() {
		return "FactionsFly";
	}
	
	@Override
	public void onPostEnable() {
		
	}
	
	@Override
	public Collection<FCommand> getCommands() {
		return new ArrayList<>();
	}
	
	@Override
	public Collection<Listener> getListeners() {
		return this.listeners;
	}
	
	@Override
	public boolean shouldEnable() {
		return Config.expansionFactionsFly.enabled == true;
	}
	
	public void cancelFlightFor(FPlayer fplayer) {
		Player player = fplayer.getPlayer();
		if (!player.isFlying()) return;
		
		// Cancel flight, teleport to the ground
		player.setFlying(false);
		player.setAllowFlight(false);
		
		Lang.EXPANSION_FACTIONSFLY_DISABLED.getBuilder().parse().sendTo(fplayer);
		
		if (Config.expansionFactionsFly.onDisableTeleportToFloor) {
			Locality floor = fplayer.getLastLocation().getFloorDown();
			if (floor == null) return; 
			
			fplayer.teleport(floor);				
		} else if (Config.expansionFactionsFly.onDisableNoFallDamage) {
			this.addFalling(player.getUniqueId());
		}
	}
	
	public boolean isFalling(UUID uuid) {
		return this.fallingPlayers.contains(uuid);
	}
	
	public boolean addFalling(UUID uuid) {
		return this.fallingPlayers.add(uuid);
	}
	
	public boolean removeFalling(UUID uuid) {
		return this.fallingPlayers.remove(uuid);
	}
	
}
