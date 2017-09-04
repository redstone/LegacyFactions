package net.redstoneore.legacyfactions.integration.dynmap;

import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.integration.dynmap.util.DynmapUtil;
import net.redstoneore.legacyfactions.util.MiscUtil;

public class DynmapStyle {

	public String lineColor = null;
	public int getLineColor() { return DynmapUtil.getColour(MiscUtil.firstNotNull(this.lineColor, Conf.dynmap.defaultStyle.lineColor, DynmapConfig.DYNMAP_STYLE_LINE_COLOR)); }
	public DynmapStyle setStrokeColor(String strokeColor) { this.lineColor = strokeColor; return this; }
	
	public Double lineOpacity = null;
	public double getLineOpacity() { return MiscUtil.firstNotNull(this.lineOpacity, Conf.dynmap.defaultStyle.lineOpacity, DynmapConfig.DYNMAP_STYLE_LINE_OPACITY); }
	public DynmapStyle setLineOpacity(Double strokeOpacity) { this.lineOpacity = strokeOpacity; return this; }
	
	public Integer lineWeight = null;
	public int getLineWeight() { return MiscUtil.firstNotNull(this.lineWeight, Conf.dynmap.defaultStyle.lineWeight, DynmapConfig.DYNMAP_STYLE_LINE_WEIGHT); }
	public DynmapStyle setLineWeight(Integer strokeWeight) { this.lineWeight = strokeWeight; return this; }
	
	public String fillColor = null;
	public int getFillColor() { return DynmapUtil.getColour(MiscUtil.firstNotNull(this.fillColor, Conf.dynmap.defaultStyle.fillColor, DynmapConfig.DYNMAP_STYLE_FILL_COLOR)); }
	public DynmapStyle setFillColor(String fillColor) { this.fillColor = fillColor; return this; }
	
	public Double fillOpacity = null;
	public double getFillOpacity() { return MiscUtil.firstNotNull(this.fillOpacity, Conf.dynmap.defaultStyle.fillOpacity, DynmapConfig.DYNMAP_STYLE_FILL_OPACITY); }
	public DynmapStyle setFillOpacity(Double fillOpacity) { this.fillOpacity = fillOpacity; return this; }
	
	public String homeMarker = null;
	public String getHomeMarker() { return MiscUtil.firstNotNull(this.homeMarker, Conf.dynmap.defaultStyle.homeMarker, DynmapConfig.DYNMAP_STYLE_HOME_MARKER); }
	public DynmapStyle setHomeMarker(String homeMarker) { this.homeMarker = homeMarker; return this; }
	
	public Boolean boost = null;
	public boolean getBoost() { return MiscUtil.firstNotNull(this.boost, Conf.dynmap.defaultStyle.boost, DynmapConfig.DYNMAP_STYLE_BOOST); }
	public DynmapStyle setBoost(Boolean boost) { this.boost = boost; return this; }

}
