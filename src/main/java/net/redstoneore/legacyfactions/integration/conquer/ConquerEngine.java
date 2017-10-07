package net.redstoneore.legacyfactions.integration.conquer;

import java.io.StreamCorruptedException;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import ch.njol.yggdrasil.Fields;
import me.andrew28.addons.conquer.api.Claim;
import me.andrew28.addons.conquer.api.ConquerFaction;
import me.andrew28.addons.conquer.api.ConquerPlayer;
import me.andrew28.addons.conquer.api.FactionsPlugin;
import me.andrew28.addons.conquer.api.FactionsPluginManager;
import me.andrew28.addons.core.Addon;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.integration.IntegrationEngine;
import net.redstoneore.legacyfactions.integration.conquer.impl.ConquerClaimImpl;
import net.redstoneore.legacyfactions.integration.conquer.impl.ConquerPlayerImpl;
import net.redstoneore.legacyfactions.integration.conquer.impl.ConquerFactionImpl;
import net.redstoneore.legacyfactions.locality.Locality;

public class ConquerEngine extends IntegrationEngine implements FactionsPlugin {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static ConquerEngine i = new ConquerEngine();
	public static ConquerEngine get() { return i; }
	
	// -------------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------------- //
	
	public static final transient String NO_MOTD_SUPPORT_WARNING = "LegacyFactions does not support MOTD, so MOTD related expressions will return null (<none>)";
	public static final transient String NO_SETID_SUPPORT_WARNING = "LegacyFactions does not support changing the identifier";
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- // 

	private boolean initialised = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 

	public void enable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Factions.get());
		FactionsPluginManager.getInstance().setFactionsPlugin(this);
	}
	
	public void disable() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public boolean canBeUsed() {
		return true;
	}

	@Override
	public void claim(ConquerFaction faction, Location location) {
		Board.get().setFactionAt(FactionColl.get().getFactionById(faction.getIdentifier()), Locality.of(location));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Claim deserializeClaim(Fields f) throws StreamCorruptedException {
		World w = Bukkit.getWorld((String) f.getObject("world"));
		Integer x = (Integer) f.getObject("X");
		Integer z = (Integer) f.getObject("Z");
	   
		return new ConquerClaimImpl(w.getChunkAt(x, z));
	}

	@Override
	public ConquerFaction deserializeFaction(Fields f) throws StreamCorruptedException {
		return ConquerFactionImpl.get(f.getObject("id").toString());
	}

	@Override
	public ConquerFaction[] getAll() {
		return FactionColl.all()
				.stream()
				.map(ConquerFactionImpl::get)
				.toArray(ConquerFaction[]::new);
	}

	@Override
	public ConquerFaction getAtLocation(Location location) {
		return ConquerFactionImpl.get(Board.get().getFactionAt(Locality.of(location)).getId());
	}

	@Override
	public ConquerFaction getByName(String faction) {
		return ConquerFactionImpl.get(FactionColl.get().getByTag(faction).getId());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Claim getClaim(Location location) {
		return new ConquerClaimImpl(location.getChunk());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Claim[] getClaims(ConquerFaction faction) {
		return Board.get().getAll(faction.getIdentifier())
			.stream()
			.toArray(ConquerClaimImpl[]::new);
	}

	@Override
	public ConquerPlayer getConquerPlayer(Player player) {
		return ConquerPlayerImpl.get(player);
	}

	@Override
	public Listener getEventWrapperListener() {
		return ConquerWrapperListener.get();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ConquerFaction getFaction(Claim claim) {
		Chunk chunk = (Chunk) claim.getRepresentationObject();
		
		return ConquerFactionImpl.get(Board.get().getFactionAt(Locality.of(chunk)));
	}

	@Override
	public boolean hasBeenInitialized() {
		return this.initialised;
	}

	@Override
	public void initialize() {
		this.initialised = true;
	}

	@Override
	public void initializeSkriptComponents(Addon addon) {
		
	}

	@Override
	public void removeClaim(Location location) {
		Board.get().removeAt(Locality.of(location));
	}

}
