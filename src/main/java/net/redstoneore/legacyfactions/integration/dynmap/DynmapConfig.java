package net.redstoneore.legacyfactions.integration.dynmap;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.redstoneore.legacyfactions.util.MiscUtil;

public class DynmapConfig {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	public final static transient String DYNMAP_STYLE_LINE_COLOR = "#00FF00";
	public final static transient double DYNMAP_STYLE_LINE_OPACITY = 0.8D;
	public final static transient int DYNMAP_STYLE_LINE_WEIGHT = 3;
	public final static transient String DYNMAP_STYLE_FILL_COLOR = "#00FF00";
	public final static transient double DYNMAP_STYLE_FILL_OPACITY = 0.35D;
	public final static transient String DYNMAP_STYLE_HOME_MARKER = "greenflag";
	public final static transient boolean DYNMAP_STYLE_BOOST = false;
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public boolean enabled = true;
	
	public String layerName = "Factions";
	
	public boolean layerVisible = true;
	
	public int layerPriority = 2;
	
	public int layerMinimumZoom = 0;
	
	public String description = 
			"<div class=\"infowindow\">\n" +
			"<span style=\"font-weight: bold; font-size: 150%;\">%factions_faction_name%</span></br>\n" +
			"<span style=\"font-style: italic; font-size: 110%;\">%factions_faction_description_blankwild%</span></br>\n" +
			"</br>\n" +
			"<span style=\"font-weight: bold;\">Leader:</span> %factions_faction_admin%</br>\n" +
			"<span style=\"font-weight: bold;\">Members:</span> %factions_faction_count_members%</br>\n" +
			"</div>";
	
	public boolean descriptionMoney = false;
	
	public boolean visibilityByFaction = true;
	
	public Set<String> visibleFactions = new LinkedHashSet<>();
	
	public Set<String> hiddenFactions = new LinkedHashSet<>();
	
	public DynmapStyle defaultStyle = new DynmapStyle()
		.setStrokeColor(DYNMAP_STYLE_LINE_COLOR)
		.setLineOpacity(DYNMAP_STYLE_LINE_OPACITY)
		.setLineWeight(DYNMAP_STYLE_LINE_WEIGHT)
		.setFillColor(DYNMAP_STYLE_FILL_COLOR)
		.setFillOpacity(DYNMAP_STYLE_FILL_OPACITY)
		.setHomeMarker(DYNMAP_STYLE_HOME_MARKER)
		.setBoost(DYNMAP_STYLE_BOOST);
	
	// Optional per Faction style overrides. Any defined replace those in dynmapDefaultStyle.
	// Specify Faction either by name or UUID.
	public Map<String, DynmapStyle> factionStyles = MiscUtil.newMap(
		"SafeZone", new DynmapStyle().setStrokeColor("#FF00FF").setFillColor("#FF00FF").setBoost(false),
		"WarZone", new DynmapStyle().setStrokeColor("#FF0000").setFillColor("#FF0000").setBoost(false)
	);
}
