package com.massivecraft.factions.integration.playervaults;

import org.bukkit.Bukkit;

import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.integration.Integration;
import com.massivecraft.factions.integration.playervaults.cmd.CmdSetMaxVaults;
import com.massivecraft.factions.integration.playervaults.cmd.CmdVault;

public class PlayerVaultsIntegration extends Integration {
	
	private static PlayerVaultsIntegration i = new PlayerVaultsIntegration();
	public static PlayerVaultsIntegration get() { return i; }
	
	private String pluginName = "PlayerVaults";
	
	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.pluginName);
	}

	@Override
	public void init() {
		// do nothing
	}
	
	public void injectCommands(FCommand onto) {
        P.get().log("Found playervaults hook, adding /f vault and /f setmaxvault commands.");
        onto.addSubCommand(new CmdSetMaxVaults());
        onto.addSubCommand(new CmdVault());

	}

}
