package net.redstoneore.legacyfactions.integration.vault;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.cmd.CmdFactions;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.integration.vault.util.VaultUtilPlayer;

public class VaultUtils extends VaultUtilPlayer {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	protected VaultUtils() {
		// Attempt to setup Vault Permissions
		try {
			RegisteredServiceProvider<Permission> registeredServiceProvider = Factions.get().getServer().getServicesManager().getRegistration(Permission.class);
			if (registeredServiceProvider != null) {
				this.perms = registeredServiceProvider.getProvider();
			}
		} catch (NoClassDefFoundError ex) {
			ex.printStackTrace();
		}
		
		// Attempt to setup Vault Economy 
		
		// TODO: Lang
		String integrationFail = "Economy integration is " + (Conf.econEnabled ? "enabled, but" : "disabled, and") + " the plugin \"Vault\" ";
		
		// Check for vault
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			Factions.get().log(integrationFail + "is not installed.");
			return;
		}
		try {
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
			
			if (rsp == null) {
				Factions.get().log(integrationFail + "is not hooked into an economy plugin.");
				return;
			}
			
			this.econ = rsp.getProvider();
		} catch (NoClassDefFoundError ex) {
			ex.printStackTrace();
		}
		
		Factions.get().log("Economy integration through Vault plugin successful.");

		// Notify them if economy plugins are present, but economy features are disabled.
		if (!Conf.econEnabled) {
			Factions.get().log("NOTE: Economy is disabled. You can enable it with the command: f config econEnabled true");
		}
		
		// Update help menu.
		CmdFactions.get().cmdHelp.updateHelp();
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public Economy getEcon() {
		return this.econ;
	}
	
	public boolean hasAtLeast(EconomyParticipator payee, double delta, String forAction) {
		if (!this.shouldBeUsed()) return true;
		
		// TODO: check if econ.has works now
		double currentBalance;
		
		if (payee instanceof FPlayer) {
			currentBalance = econ.getBalance(((FPlayer) payee).getPlayer());
		} else if (this.isUUID(payee.getAccountId())) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(payee.getAccountId()));
			if (offlinePlayer != null) {
				currentBalance = econ.getBalance(offlinePlayer);
			} else {
				currentBalance = 0;
			}
		} else {
			currentBalance = this.getBalance(payee.getAccountId());
		}

		if (currentBalance < delta) {
			if (forAction != null && !forAction.isEmpty()) {
				payee.sendMessage("<h>%s<i> can't afford <h>%s<i> %s.", payee.describeTo(payee, true), moneyString(delta), forAction);
			}
			
			return false;
		}
		
		return true;
	}
	
	public void sendBalanceInfo(EconomyParticipator receiver, EconomyParticipator about) {
		if (!this.shouldBeUsed()) {
			Factions.get().warn("Vault does not appear to be hooked into an economy plugin.");
			return;
		}
		
		// TODO: lang
		receiver.sendMessage("<a>%s's<i> balance is <h>%s<i>.", about.describeTo(receiver, true), this.moneyString(this.getBalance(about.getAccountId())));
	}
	
}
