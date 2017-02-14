package com.massivecraft.factionsuuid.integration.playervaults;

import org.bukkit.Bukkit;

import com.massivecraft.factionsuuid.Factions;
import com.massivecraft.factionsuuid.cmd.FCommand;
import com.massivecraft.factionsuuid.integration.Integration;
import com.massivecraft.factionsuuid.integration.playervaults.cmd.CmdSetMaxVaults;
import com.massivecraft.factionsuuid.integration.playervaults.cmd.CmdVault;

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
        Factions.get().log("Found playervaults hook, adding /f vault and /f setmaxvault commands.");
        onto.addSubCommand(new CmdSetMaxVaults());
        onto.addSubCommand(new CmdVault());

	}

}
