package net.redstoneore.legacyfactions.util.cross;

public abstract interface Cross<T> {
	
	/**
	 * Does this cross object match another cross item
	 * @param what Object to compare to
	 * @return true if objects match
	 */
	public abstract boolean is(T what);
	
}
