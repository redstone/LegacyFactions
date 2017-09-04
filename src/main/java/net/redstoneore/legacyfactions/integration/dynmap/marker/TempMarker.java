package net.redstoneore.legacyfactions.integration.dynmap.marker;

import java.util.Optional;

import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import net.redstoneore.legacyfactions.integration.dynmap.util.DynmapUtil;

public class TempMarker {
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public String label;
	public String world;
	public double x;
	public double y;
	public double z;
	public String iconName;
	public String description;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Create a marker.
	 * @param markerApi The MarkerAPI
	 * @param markerSet The MarkerSet
	 * @param markerId The Marker id
	 * @return an {@link Optional} contain the {@link Marker}
	 */
	public Optional<Marker> create(MarkerAPI markerApi, MarkerSet markerSet, String markerId) {
		Marker marker = markerSet.createMarker(
			markerId,
			this.label,
			this.world,
			this.x,
			this.y,
			this.z,
			DynmapUtil.getMarkerIcon(markerApi, this.iconName),
			false
		);
		
		if (marker == null) return Optional.empty();
		
		marker.setDescription(this.description);
		
		return Optional.of(marker);
	}
	
	public void update(MarkerAPI markerApi, MarkerSet markerset, Marker marker) {
		if (!this.equals(marker)) {
			marker.setLocation(this.world, this.x, this.y, this.z );
		}
		
		if (!marker.getLabel().equalsIgnoreCase(this.label)) {
			marker.setLabel(this.label);
		}
		
		MarkerIcon icon = DynmapUtil.getMarkerIcon(markerApi, this.iconName);
		if (!marker.getMarkerIcon().equals(icon)) {
			marker.setMarkerIcon(icon);
		}
		
		if (!marker.getDescription().equals(this.description)) {
			marker.setDescription(this.description);
		}
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public boolean equals(Marker marker) {
		return marker.getWorld() == this.world && marker.getX() == this.x && marker.getY() == this.y && marker.getZ() == this.z;
	}

}
