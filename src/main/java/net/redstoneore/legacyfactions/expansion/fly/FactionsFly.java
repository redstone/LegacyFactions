package net.redstoneore.legacyfactions.expansion.fly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.expansion.FactionsExpansion;
import net.redstoneore.legacyfactions.locality.Locality;

public class FactionsFly extends FactionsExpansion {
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static boolean canFlyHere(FPlayer fplayer, FLocation location) {
		Relation relation = fplayer.getRelationTo(Board.get().getFactionAt(location));
		
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
	
	public void cancelFlightFor(FPlayer fplayer) {
		Player player = fplayer.getPlayer();
		if (!player.isFlying()) return;
		
		// Cancel flight, teleport to the ground
		player.setFlying(false);
		player.setAllowFlight(false);
		
		fplayer.sendMessage(Factions.get().getTextUtil().parse(Lang.EXPANSION_FACTIONS_FLY_DISABLED.toString()));
		 
		if (Conf.factionsFlyTeleportToFloorOnDisable) {
			Locality floor = fplayer.getLastLocation().getFloorDown();
			if (floor == null) return; 
			
			fplayer.teleport(floor);				
		} else if (Conf.factionsFlyNoFirstFallDamage) {
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
