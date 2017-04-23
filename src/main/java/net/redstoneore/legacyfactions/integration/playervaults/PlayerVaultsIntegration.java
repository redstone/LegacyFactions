package net.redstoneore.legacyfactions.integration.playervaults;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.integration.Integration;
import net.redstoneore.legacyfactions.integration.playervaults.cmd.CmdSetMaxVaults;
import net.redstoneore.legacyfactions.integration.playervaults.cmd.CmdVault;

public class PlayerVaultsIntegration extends Integration {
	
	private static PlayerVaultsIntegration i = new PlayerVaultsIntegration();
	public static PlayerVaultsIntegration get() { return i; }
	
	@Override
	public String getName() {
		return "PlayerVaults";
	}
	
	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.getName());
	}

	@Override
	public void init() {
		// Just notify we're here
		this.notifyEnabled();
	}
	
	public void injectCommands(FCommand onto) {
        Factions.get().log("Found playervaults hook, adding /f vault and /f setmaxvault commands.");
        onto.addSubCommand(new CmdSetMaxVaults());
        onto.addSubCommand(new CmdVault());

	}

}
