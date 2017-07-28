package net.redstoneore.legacyfactions;


import net.redstoneore.legacyfactions.entity.VaultAccount;

/**
 * Interface given to entities are participants in economy actions.
 */
public interface EconomyParticipator extends RelationParticipator {
	
	// -------------------------------------------------- //
	// INTERFACE
	// -------------------------------------------------- //
	
	/**
	 * Returns the account id associated with this participator
	 * @return the account id associated with this participator
	 */
	String getAccountId();
	
	void sendMessage(String str, Object... args);
	
	void sendMessage(Lang translation, Object... args);
	
	// -------------------------------------------------- //
	// DEFAULTS
	// -------------------------------------------------- //
	
	default VaultAccount getVaultAccount() {
		return VaultAccount.get(this);
	}
	
	// -------------------------------------------------- //
	// AMBIGIOUS DEPRECATED METHODS
	// -------------------------------------------------- //
	
	/**
	 * Deprecated, use sendMessage
	 */
	@Deprecated
	public void msg(String str, Object... args);
	
	/**
	 * Deprecated, use sendMessage
	 */
	@Deprecated
	public void msg(Lang translation, Object... args);
	
	// -------------------------------------------------- //
	// AMBIGIOUS DEPRECATED METHODS
	// -------------------------------------------------- //

}
