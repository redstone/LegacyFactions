package net.redstoneore.legacyfactions.util;

@FunctionalInterface
public interface ConditionalBoolean {
	
	public static ConditionalBoolean of(ConditionalBoolean of) {
		return of;
	}
	
	boolean get();

}
