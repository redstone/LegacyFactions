package net.redstoneore.legacyfactions.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.callback.Callback;
import net.redstoneore.legacyfactions.locality.LocalityLazy;
import net.redstoneore.legacyfactions.locality.Locality;

public class NewSpiralTask {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static NewSpiralTask of(Locality locality, int radius) {
		return new NewSpiralTask(locality, radius);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public NewSpiralTask(Locality locality, int radius) {
		this.world = locality.getWorld().getUID();
		this.x = locality.getChunkX();
		this.z = locality.getChunkZ();	
		this.radius = radius;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private transient final UUID world;
	private transient final int x, z, radius;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public void then(final Callback<List<Locality>> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), () -> {
			// Async
			
			boolean working = true;
			final List<Locality> chunks = new LinkedList<>();
			
			// add the first location
			chunks.add(LocalityLazy.of(this.world, this.x, this.z));
			
			boolean isZLeg = false;
			boolean isNeg = false;
			
			int length = -1;
			int current = 0;
			int limit = (this.radius - 1) * 2;
			
			int xAt = this.x;
			int zAt = this.z;
			
			while (working) {
				// Make sure we're still in the radius, otherwise we wrap up
				if (!(current < limit)) {
					working = false;
					break;
				}
				
				// Make sure we don't need to turn down the next leg of the spiral
				if (current < length) {
					current++;

					// If we're outside the radius we're done
					if (!(current < limit)) {
						working = false;
						break;
					}
				} else {	// one leg/side of the spiral down...
					current = 0;
					isZLeg ^= true;
					// every second leg (between X and Z legs, negative or positive), length increases
					if (isZLeg) {
						isNeg ^= true;
						length++;
					}
				}

				// move one chunk further in the appropriate direction
				if (isZLeg) {
					zAt += (isNeg) ? -1 : 1;
				} else {
					xAt += (isNeg) ? -1 : 1;
				}

				chunks.add(LocalityLazy.of(this.world, xAt, zAt));
			}
			
			// Return it back next tick
			Bukkit.getScheduler().runTask(Factions.get(), () -> {
				callback.then(chunks, Optional.empty());
			});
		});
	}
	
}
