package net.redstoneore.legacyfactions.entity.persist.shared;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

public abstract class SharedFPlayerColl extends FPlayerColl {

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public Collection<FPlayer> getOnlinePlayers() {
		if (Bukkit.getServer().getOnlinePlayers().isEmpty()) {
			return new HashSet<>();
		}
		return Bukkit.getServer().getOnlinePlayers().stream()
			.map(this::getByPlayer)
			.collect(Collectors.toSet());
	}

	@Override
	public FPlayer getByPlayer(Player player) {
		return this.getById(player.getUniqueId().toString());
	}
	
	@Override
	public FPlayer getByOfflinePlayer(OfflinePlayer player) {
		return this.getById(player.getUniqueId().toString());
	}
	
}
