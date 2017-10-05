package net.redstoneore.legacyfactions.integration.vault;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.cmd.CmdFactionsHelp;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.integration.vault.util.VaultUtilPlayer;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

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
		
		// Check for vault
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			if (Config.econEnabled) {
				Factions.get().log(Lang.ECON_ERROR_ONE.toString());
			} else {
				Factions.get().log(Lang.ECON_ERROR_TWO.toString());

			}
			return;
		}
		try {
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
			
			if (rsp == null) {
				if (Config.econEnabled) {
					Factions.get().log(Lang.ECON_ERROR_THREE.toString());
				} else {
					Factions.get().log(Lang.ECON_ERROR_FOUR.toString());
				}
				return;
			}
			
			this.econ = rsp.getProvider();
		} catch (NoClassDefFoundError ex) {
			ex.printStackTrace();
		}
		
		Factions.get().log("Economy integration through Vault plugin successful.");

		// Notify them if economy plugins are present, but economy features are disabled.
		if (!Config.econEnabled) {
			Factions.get().log("NOTE: Economy is disabled. You can enable it with the command: f config econEnabled true");
		}
		
		// Update help menu.
		CmdFactionsHelp.get().updateHelp();
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
		} else {
			currentBalance = this.getBalance(payee.getAccountId());
		}
		
		if (currentBalance < delta) {
			if (forAction != null && !forAction.isEmpty()) {
				String message = Lang.ECON_CANTAFFORD.toString();
				message = message.replace("<player>", payee.describeTo(payee, true));
				message = message.replace("<amount>", moneyString(delta));
				message = message.replace("<forwhat>", forAction);
				
				payee.sendMessage(TextUtil.parseColor(message));
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
		
		String message = Lang.ECON_BALANCE.toString();
		message = message.replace("<player>", about.describeTo(receiver, true));
		message = message.replace("<amount>", this.moneyString(this.getBalance(about.getAccountId())));
		
		receiver.sendMessage(TextUtil.parseColor(message));
	}
	
	public Permission getPerms() {
		return this.perms;
	}
	
}
