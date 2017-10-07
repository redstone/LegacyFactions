package net.redstoneore.legacyfactions.integration.conquer.impl;

import org.bukkit.Chunk;

import ch.njol.skript.localization.Language;
import ch.njol.yggdrasil.Fields;
import me.andrew28.addons.conquer.api.Claim;
import me.andrew28.addons.conquer.api.ConquerFaction;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.locality.Locality;

public class ConquerClaimImpl extends Claim<Chunk> {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public ConquerClaimImpl(Locality locality) {
		this.locality = locality;
		this.chunk = locality.getChunk();
	}
	
	public ConquerClaimImpl(Chunk chunk) {
		this.locality = Locality.of(chunk);
		this.chunk = chunk;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final Chunk chunk;
	private final Locality locality;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public Chunk getRepresentationObject() {
		return this.chunk;
	}

	@Override
	public boolean isSafeZone() {
		return Board.get().getFactionAt(this.locality).isSafeZone();
	}

	@Override
	public boolean isWarZone() {
		return Board.get().getFactionAt(this.locality).isWarZone();
	}

	@Override
	public String representationObjectToString(Chunk representationObject, int flags) {
		return String.format("Conquer Claim%s: Chunk[x: %d, z: %d, world %s]",
				((flags & Language.F_PLURAL) != 0) ? "s" : "",
				representationObject.getX(),
				representationObject.getZ(),
				representationObject.getWorld().getName());
	}

	@Override
	public void resetFaction() {
		Board.get().removeAt(this.locality);
	}

	@Override
	public Fields serialize() {
		Fields f = new Fields();
		f.putObject("world", this.chunk.getWorld().getName());
		f.putPrimitive("X", this.chunk.getX());
		f.putPrimitive("Z", this.chunk.getZ());
		return f;	
	}

	@Override
	public void setFaction(ConquerFaction faction) {
		Board.get().setFactionAt(FactionColl.get(faction.getName()), this.locality);
	}

}
