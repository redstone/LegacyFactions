package net.redstoneore.legacyfactions.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {
	
	/**
	 * Round a double
	 * @param value Number to round.
	 * @param places Places to round to.
	 * @return
	 */
	public static double roundUp(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}
