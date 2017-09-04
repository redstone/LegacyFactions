package net.redstoneore.legacyfactions.integration.dynmap.marker;

import java.util.Optional;

import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class TempMarkerSet {

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public String label;
	public int minimumZoom;
	public int priority;
	public boolean hideByDefault;
	
	// -------------------------------------------- //
	// METHODS
	// -------------------------------------------- //
	
	/**
	 * Create a MarkerSet
	 * @param markerApi MarkerAPI
	 * @param id MarkerSet id
	 * @return {@link Optional} containing the MarkerSet if it was successful 
	 */
	public Optional<MarkerSet> create(MarkerAPI markerApi, String id) {
		MarkerSet marker = markerApi.createMarkerSet(id, this.label, null, false);
		
		if (marker == null) return Optional.empty();
		
		// Minimum Zoom
		if (this.minimumZoom > 0) {
			marker.setMinZoom(this.minimumZoom);
		}

		// Priority
		marker.setLayerPriority(this.priority);

		// Hide by Default
		marker.setHideByDefault(this.hideByDefault);
		
		return Optional.of(marker);
	}
	
	public void update(MarkerAPI markerApi, MarkerSet markerset) {
		// Label
		if (!markerset.getMarkerSetLabel().equals(this.label)) {
			markerset.setMarkerSetLabel(this.label);
		}

		// Minimum Zoom
		if (this.minimumZoom > 0 && markerset.getMinZoom() != this.minimumZoom) {
			markerset.setMinZoom(this.minimumZoom);
		}

		// Priority
		if (markerset.getLayerPriority() != this.priority) {
			markerset.setLayerPriority(this.priority);
		}

		// Hide by Default
		if (markerset.getHideByDefault() != this.hideByDefault) {
			markerset.setHideByDefault(this.hideByDefault);
		}
	}
	
}
