package net.redstoneore.legacyfactions.expansion.fly;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.expansion.FactionsExpansion;

public class FactionsFly extends FactionsExpansion {
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static boolean canFlyHere(FPlayer fplayer, FLocation location) {
		switch (fplayer.getRelationTo(Board.get().getFactionAt(location))) {
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
	
	private Collection<Listener> listeners = Lists.newArrayList(
		FactionsFlyListener.get(),
		FactionsFlyCommand.get()
	);
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
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
	
	public static Location getFloor(Location location) {
		Location newLocation = location.clone();
		for (int i = location.getBlockY(); i > 0; i--) {
			newLocation.setY(i);
			newLocation.setY(newLocation.getY() + 1.0D);
			newLocation.setYaw(location.getYaw());
			newLocation.setPitch(location.getPitch());
			
			if (newLocation.getBlock().getType() != Material.AIR) {
				newLocation = new Location(location.getWorld(), newLocation.getX(), newLocation.getY() + 2.0D, newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
			}
		}
		
		return newLocation;

	}
	
}
