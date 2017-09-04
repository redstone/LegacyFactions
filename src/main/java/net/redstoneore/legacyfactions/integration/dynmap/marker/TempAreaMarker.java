package net.redstoneore.legacyfactions.integration.dynmap.marker;

import java.util.Optional;

import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import net.redstoneore.legacyfactions.integration.dynmap.util.DynmapUtil;

public class TempAreaMarker {
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public String label;
	public String world;
	public double x[];
	public double z[];
	public String description;
	
	public int lineColor;
	public double lineOpacity;
	public int lineWeight;
	
	public int fillColor;
	public double fillOpacity;
	
	public boolean boost;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Create a marker
	 * @param markerApi The MarkerAPI
	 * @param markerset The MarketSet
	 * @param markerId The marker id
	 * @return an {@link Optional} containing the {@link AreaMarker} if it was a success
	 */
	public Optional<AreaMarker> create(MarkerAPI markerApi, MarkerSet markerset, String markerId) {
		AreaMarker marker = markerset.createAreaMarker(
			markerId,
			this.label,
			false,
			this.world,
			this.x,
			this.z,
			false 
		);
		
		if (marker == null) return Optional.empty();
		
		// Description
		marker.setDescription(this.description);
		
		// Line Style
		marker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
		
		// Fill Style
		marker.setFillStyle(this.fillOpacity, this.fillColor);
		
		// Boost Flag
		marker.setBoostFlag(this.boost);
		
		return Optional.of(marker);
	}
	
	/**
	 * Update a Marker
	 * @param markerApi The MarkerAPI
	 * @param markerset The MarkerSet
	 * @param marker The AreaMarker
	 */
	public void update(MarkerAPI markerApi, MarkerSet markerset, AreaMarker marker) {
		// Corner Locations
		if (!DynmapUtil.equals(marker, this.x, this.z))
		{
			marker.setCornerLocations(this.x, this.z);			
		}
		
		// Label
		if (!marker.getLabel().equals(this.label)) {
			marker.setLabel(this.label);
		}
		
		// Description
		if (!marker.getDescription().equals(this.description)) {
			marker.setDescription(this.description);
		}
		
		// Line Style
		Integer lineWeight = marker.getLineWeight();
		Double lineOpacity = marker.getLineOpacity();
		Integer lineColor = marker.getLineColor();
		
		if (!lineWeight.equals(this.lineWeight) || !lineOpacity.equals(this.lineOpacity) || !lineColor.equals(this.lineColor)) {
			marker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
		}
		
		// Fill Style
		Double fillOpacity = marker.getFillOpacity();
		Integer fillColor = marker.getFillColor();
		
		if (!fillOpacity.equals(this.fillOpacity) || !fillColor.equals(this.fillColor)) {
			marker.setFillStyle(this.fillOpacity, this.fillColor);
		}
		
		// Boost Flag
		if (marker.getBoostFlag() != this.boost) {
			marker.setBoostFlag(this.boost);
		}
	}
	
}
