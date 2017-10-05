package net.redstoneore.legacyfactions.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

@SuppressWarnings("deprecation")
public class VaultAccount {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	protected static Map<String, VaultAccount> accounts = new HashMap<>();
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	/**
	 * Leave this as package private
	 * @param economyParticipator
	 * @return
	 */
	public static VaultAccount get(EconomyParticipator economyParticipator) {
		if (!accounts.containsKey(economyParticipator.getAccountId())) {
			if (economyParticipator instanceof FPlayer) {
				// Is FPlayer
				accounts.put(economyParticipator.getAccountId(), new VaultAccount(economyParticipator, ((FPlayer) economyParticipator).getPlayer()));
			}  else {
				try {
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(economyParticipator.getAccountId()));
					accounts.put(economyParticipator.getAccountId(), new VaultAccount(economyParticipator, offlinePlayer));
				} catch (Exception e) {
					accounts.put(economyParticipator.getAccountId(), new VaultAccount(economyParticipator, economyParticipator.getAccountId()));
				}
			}
		}
		
		return accounts.get(economyParticipator.getAccountId());
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private VaultAccount(EconomyParticipator economyParticipator, String named) {
		this.named = named;
		this.economyParticipator = economyParticipator;
	}
	
	private VaultAccount(EconomyParticipator economyParticipator, OfflinePlayer offlinePlayer) {
		this.offlinePlayer = offlinePlayer;
		this.economyParticipator = economyParticipator;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final EconomyParticipator economyParticipator;
	
	private String named = null;
	private OfflinePlayer offlinePlayer = null;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	public EconomyParticipator getEconomyParticipator() {
		return this.economyParticipator;
	}
	
	public String getAccountId() {
		return this.getEconomyParticipator().getAccountId();
	}
	
	public boolean has(double amount) {
		if (this.offlinePlayer != null) {
			return VaultEngine.getUtils().getEcon().has(this.offlinePlayer, amount);
		} else {
			return VaultEngine.getUtils().getEcon().has(this.named, amount);
		}
	}
	
	public boolean transfer(VaultAccount invoker, double amount, VaultAccount to, boolean notify) {
		// If the invoker can control this account ...
		if (!VaultEngine.getUtils().canIControllYou(invoker.getEconomyParticipator(), this.getEconomyParticipator())) return false;
		
		// ... and they have enough in their balance ...
		if (!this.has(amount)) {
			if (invoker != null && notify) {
				String message = Lang.ECON_TRANSFER_CANTAFFORD.toString();
				message = message.replace("<from>", this.getEconomyParticipator().describeTo(invoker.getEconomyParticipator(), true));
				message = message.replace("<amount>", VaultEngine.getUtils().moneyString(amount));
				message = message.replace("<target>", to.getEconomyParticipator().describeTo(invoker.getEconomyParticipator()));
				invoker.getEconomyParticipator().sendMessage(TextUtil.parseColor(message));
			}
			return false;
		}
		
		// .. attempt a withdraw, if we couldn't withdraw, we don't continue. 
		if (!this.withdraw(amount)) {
			if (notify) {
				String message = Lang.ECON_TRANSFER_UNABLE.toString();
				message = message.replace("<amount>", VaultEngine.getUtils().moneyString(amount));
				message = message.replace("<target>", to.getEconomyParticipator().describeTo(invoker.getEconomyParticipator()));
				message = message.replace("<from>", this.getEconomyParticipator().describeTo(invoker.getEconomyParticipator(), true));
				
				invoker.getEconomyParticipator().sendMessage(TextUtil.parseColor(message));
			}
			
			return false;
		}
		
		if (to.deposit(amount)) {
			// Success.
			if (notify) {
				VaultEngine.getUtils().sendTransferInfo(invoker.getEconomyParticipator(), this.getEconomyParticipator(), to.getEconomyParticipator(), amount);
			}
			return true;
		} else {
			// Transaction failed, refund account
			this.deposit(amount);
			return false;
		}
	}
	
	public boolean transfer(VaultAccount invoker, double amount, VaultAccount to) {
		return this.transfer(invoker, amount, to, true);
	}
	
	public boolean transfer(double amount, VaultAccount to) {
		return this.transfer(this, amount, to);
	}
	
	public boolean transfer(double amount, VaultAccount to, boolean notify) {
		return this.transfer(this, amount, to, true);
	}

	public double getBalance() {
		if (this.offlinePlayer != null) {
			return VaultEngine.getUtils().getEcon().getBalance(this.offlinePlayer);
		} else {
			return VaultEngine.getUtils().getEcon().getBalance(this.named);
		}
	}
	
	public boolean deposit(double amount) {
		if (this.offlinePlayer != null) {
			return VaultEngine.getUtils().getEcon().depositPlayer(this.offlinePlayer, amount).transactionSuccess();
		} else {
			return VaultEngine.getUtils().getEcon().depositPlayer(this.named, amount).transactionSuccess();
		}
	}
	
	public boolean withdraw(double amount) {
		if (this.offlinePlayer != null) {
			return VaultEngine.getUtils().getEcon().withdrawPlayer(this.offlinePlayer, amount).transactionSuccess();
		} else {
			return VaultEngine.getUtils().getEcon().withdrawPlayer(this.named, amount).transactionSuccess();
		}
	}
	
}
