package net.redstoneore.legacyfactions.struct;

public class LandValue {
	
	public static LandValue create(String faction, int radius, double additionalCost) {
		LandValue value = new LandValue();
		value.faction = faction;
		value.radius = radius;
		value.additionalCost = additionalCost;
		return value;
	}
	
	public String faction = "";
	public int radius = 0;
	public double additionalCost = 0;
	
}
