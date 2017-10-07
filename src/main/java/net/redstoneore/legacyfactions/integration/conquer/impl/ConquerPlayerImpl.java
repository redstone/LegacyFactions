package net.redstoneore.legacyfactions.integration.conquer.impl;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.andrew28.addons.conquer.api.ConquerFaction;
import me.andrew28.addons.conquer.api.ConquerPlayer;
import me.andrew28.addons.conquer.api.PowerChangeable;
import me.andrew28.addons.conquer.api.Role;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

public class ConquerPlayerImpl extends ConquerPlayer implements PowerChangeable {

	// -------------------------------------------------- //
	// STATIC
	// -------------------------------------------------- //
	
	private static transient Map<UUID, ConquerPlayer> players = new ConcurrentHashMap<>();
	
	public static ConquerPlayer get(Player player) {
		UUID id = player.getUniqueId();
		if (!players.containsKey(id)) {
			players.put(id, new ConquerPlayerImpl(player));
		}
		return players.get(id);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private ConquerPlayerImpl(Player player) {
		super(player);
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public double getMaximumPower() {
		return FPlayerColl.get(this.getPlayer()).getPowerMax();
	}

	@Override
	public double getPower() {
		return FPlayerColl.get(this.getPlayer()).getPower();
	}

	@Override
	public double getPowerBoost() {
		return FPlayerColl.get(this.getPlayer()).getPowerBoost();
	}

	@Override
	public void resetPower() {
		this.setPower(0);
	}

	@Override
	public void resetPowerBoost() {
		this.setPowerBoost(0);
	}

	@Override
	public void setPower(double power) {
		FPlayerColl.get(this.getPlayer()).alterPower(power);
		
	}

	@Override
	public void setPowerBoost(double powerBoost) {
		FPlayerColl.get(this.getPlayer()).setPowerBoost(powerBoost);
	}

	@Override
	public boolean getAutomaticMapUpdateMode() {
		return FPlayerColl.get(this.getPlayer()).isMapAutoUpdating();
	}

	@Override
	public ConquerFaction getFaction() {
		return ConquerFactionImpl.get(FPlayerColl.get(this.getPlayer()).getFactionId());
	}

	@Override
	public Date getLastActivity() {
		return new Date(FPlayerColl.get(this.getPlayer()).getLastLoginTime());
	}

	@Override
	public double getMinimumPower() {
		return FPlayerColl.get(this.getPlayer()).getPowerMin();
	}

	@Override
	public Role getRole() {
		FPlayer fplayer = FPlayerColl.get(this.getPlayer());
		
		if (fplayer.getRole() == net.redstoneore.legacyfactions.Role.COLEADER) {
			return Role.OTHER;
		}
		
		return Role.valueOf(fplayer.getRole().name());
	}

	@Override
	public String getTitle() {
		return FPlayerColl.get(this.getPlayer()).getTitle();
	}

	@Override
	public boolean hasFaction() {
		return FPlayerColl.get(this.getPlayer()).hasFaction();
	}

	@Override
	public boolean isAutoClaiming() {
		return FPlayerColl.get(this.getPlayer()).getAutoClaimFor() != null;
	}

	@Override
	public void resetFaction() {
		FPlayerColl.get(this.getPlayer()).setFaction(FactionColl.get().getWilderness());
	}

	@Override
	public void resetTitle() {
		FPlayerColl.get(this.getPlayer()).setTitle("");
	}

	@Override
	public void setAutoClaiming(Boolean autoClaim) {
		FPlayer fplayer = FPlayerColl.get(this.getPlayer());
		
		if (autoClaim) {
			fplayer.setAutoClaimFor(fplayer.getFaction());
		} else {
			fplayer.setAutoClaimFor(null);
		}
	}

	@Override
	public void setAutomaticMapUpdateMode(Boolean mapAutoUpdating) {
		FPlayerColl.get(this.getPlayer()).setMapAutoUpdating(mapAutoUpdating);
	}

	@Override
	public void setFaction(ConquerFaction faction) {
		FPlayerColl.get(this.getPlayer()).setFaction(FactionColl.get().getByTag(faction.getName()));
	}

	@Override
	public void setLastActivity(Date lastLoginTime) {
		FPlayerColl.get(this.getPlayer()).setLastLoginTime(lastLoginTime.getTime());
	}

	@Override
	public void setRole(Role role) {
		FPlayerColl.get(this.getPlayer()).setRole(net.redstoneore.legacyfactions.Role.valueOf(role.name()));
	}

	@Override
	public void setTitle(String title) {
		FPlayerColl.get(this.getPlayer()).setTitle(title);
	}

}
