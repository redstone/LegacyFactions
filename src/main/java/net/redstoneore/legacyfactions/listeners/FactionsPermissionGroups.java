package net.redstoneore.legacyfactions.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsChangedTerritory;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.util.LocationUtil;

public class FactionsPermissionGroups implements Listener {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsPermissionGroups i = new FactionsPermissionGroups();
	public static FactionsPermissionGroups get() { return i; }
	private FactionsPermissionGroups() { }
	
	// -------------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------------- //
	
	private static List<String> GROUPS = Lists.newArrayList(
		"legacyfactions_" + Relation.ALLY.nicename.toLowerCase(),
		"legacyfactions_" + Relation.ENEMY.nicename.toLowerCase(),
		"legacyfactions_" + Relation.MEMBER.nicename.toLowerCase(),
		"legacyfactions_" + Relation.NEUTRAL.nicename.toLowerCase(),
		"legacyfactions_" + Relation.TRUCE.nicename.toLowerCase(),
		"legacyfactions_wilderness",
		"legacyfactions_warzone",
		"legacyfactions_safezone"
		
	);
	
	// -------------------------------------------------- //
	// EVENT
	// -------------------------------------------------- //
	
	@EventHandler
	public void setGroupOnLogin(PlayerJoinEvent event) {
		this.setGroup(event.getPlayer());
	}
	
	@EventHandler
	public void setGroupOnTerritoryChange(EventFactionsChangedTerritory event) {
		this.setGroup(event.getFPlayer(), event.getFactionTo());
	}
	
	public void setGroup(Player player) {
		FPlayer fplayer = FPlayerColl.get(player);
		Faction factionHere = fplayer.getLastLocation().getFactionHere();

		this.setGroup(fplayer, factionHere);
	}
	
	public void setGroup(FPlayer fplayer, Faction factionHere) {
		if (LocationUtil.isFactionsDisableIn(fplayer.getLastLocation().getLocation())) {
			this.setGroup(fplayer, "");
			return;
		}
		
		if (factionHere.isSafeZone()) {
			this.setGroup(fplayer, "legacyfactions_safezone");
			return;
		}
		
		if (factionHere.isWarZone()) {
			this.setGroup(fplayer, "legacyfactions_warzone");
			return;
		}
		
		if (factionHere.isWilderness()) {
			this.setGroup(fplayer, "legacyfactions_wilderness");
			return;
		}
		
		Relation relation = factionHere.getRelationTo(fplayer);
		this.setGroup(fplayer, "legacyfactions_" + relation.name().toLowerCase());
	}
	
	public void setGroup(FPlayer fplayer, String groupName) {		
		GROUPS.stream()
			.filter(group -> group != groupName)
			.forEach(group -> 
				VaultEngine.getUtils().getPerms().playerRemoveGroup(fplayer.getPlayer(), group)
			);
		
		if (groupName == null || groupName == "") return;
		
		if (!Lists.newArrayList(VaultEngine.getUtils().getPerms().getPlayerGroups(fplayer.getPlayer())).contains(groupName)) {
			VaultEngine.getUtils().getPerms().playerAddGroup(fplayer.getPlayer(), groupName);
		}
	}
	
}