package net.redstoneore.legacyfactions.integration.dynmap.util;

import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.integration.dynmap.DynmapConfig;
import net.redstoneore.legacyfactions.integration.dynmap.DynmapStyle;

public class DynmapUtil {
	
	/**
	 * Get the Dynmap marker icon
	 * @param markerApi The MarkerAPI
	 * @param name Name of this marker icon
	 * @return {@link MarkerIcon}
	 */
	public static MarkerIcon getMarkerIcon(MarkerAPI markerApi, String name) {
		MarkerIcon markerIcon = markerApi.getMarkerIcon(name);
		if (markerIcon == null) {
			markerIcon = markerApi.getMarkerIcon(DynmapConfig.DYNMAP_STYLE_HOME_MARKER);
		}
		return markerIcon;
	}
	
	/**
	 * Get the Dynmap style colour
	 * @param string String format 
	 * @return int colour
	 */
	public static int getColour(String string) {
		int colour = 0x00FF00;
		try {
			colour = Integer.parseInt(string.substring(1), 16);
		} catch (NumberFormatException nfx) {
			
		}
		return colour;
	}
	
	/**
	 * Does an area marker equal and x and z
	 * @param marker The marker. 
	 * @param x X
	 * @param z Z
	 * @return true if they equal
	 */
	public static boolean equals(AreaMarker marker, double x[], double z[]) {
		int length = marker.getCornerCount();
		
		if (x.length != length) return false;
		if (z.length != length) return false;
		
		for (int i = 0; i < length; i++) {
			if (marker.getCornerX(i) != x[i]) return false;
			if (marker.getCornerZ(i) != z[i]) return false;
		}
		
		return true;
	}
	
	/**
	 * get the DynmapStyle for a faction
	 * @param faction The faction to get the style for.
	 * @return {@link DynmapStyle}
	 */
	public static DynmapStyle getStyle(Faction faction) {
		DynmapStyle style;

		style = Config.dynmap.factionStyles.get(faction.getId());
		if (style != null) return style;

		style = Config.dynmap.factionStyles.get(faction.getTag());
		if (style != null) return style;

		return Config.dynmap.defaultStyle;
	}


}
