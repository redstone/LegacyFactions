package net.redstoneore.legacyfactions;

/**
 * Interface given to entities are participate in economy actions.
 */
public interface EconomyParticipator extends RelationParticipator {
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Returns the account id associated with this participator
	 * @return the account id associated with this participator
	 */
    public String getAccountId();

    public void msg(String str, Object... args);

    public void msg(Lang translation, Object... args);
    
}
