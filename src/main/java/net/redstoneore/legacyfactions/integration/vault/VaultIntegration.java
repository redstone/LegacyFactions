package net.redstoneore.legacyfactions.integration.vault;

import net.redstoneore.legacyfactions.integration.Integration;

public class VaultIntegration extends Integration {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static VaultIntegration i = new VaultIntegration();
	public static VaultIntegration get() { return i; }
		
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public String getName() {
		return "Vault";
	}
	
	@Override
	public void init() {
		VaultEngine.getUtils();
		this.notifyEnabled();
	}
	
	public boolean hasPermissions() {
		return VaultEngine.getUtils().getPerms() != null;
	}
	
}
