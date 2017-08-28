package net.redstoneore.legacyfactions.integration.vault.util;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.EconomyResponse;
import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.integration.vault.VaultUtils;
import net.redstoneore.legacyfactions.mixin.BukkitMixin;
import net.redstoneore.legacyfactions.util.RelationUtil;
import net.redstoneore.legacyfactions.util.TextUtil;

public abstract class VaultUtilBase {

	public static String DEFAULT_BALANCE_STRING = "0";
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	protected net.milkbowl.vault.economy.Economy econ = null;
	protected net.milkbowl.vault.permission.Permission perms = null;
	
	protected final DecimalFormat format = new DecimalFormat("#,###");
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public boolean shouldBeUsed() {
		return Conf.econEnabled && this.econ != null && this.econ.isEnabled();
	}
	
	/**
	 * Format money string based on server's set currency type, like "24 gold" or "$24.50"
	 * @param amount Amount to format.
	 * @return String of currency in format
	 */
	public String moneyString(double amount) {
		return this.econ.format(amount);
	}
	
	 /**
	  * Calculate refund amount for unclaiming land
	  * @param amountChunks Amount
	  * @return double of amount
	  */
	public double calculateClaimRefund(int amountChunks) {
		return calculateClaimCost(amountChunks - 1, false) * Conf.econClaimRefundMultiplier;
	}

	/**
	 * Calculate value of all owned land
	 * @param amountChunks Amount
	 * @return double of amount
	 */
	public double calculateTotalLandValue(int amountChunks) {
		double amount = 0;
		for (int x = 0; x < amountChunks; x++) {
			amount += calculateClaimCost(x, false);
		}
		return amount;
	}

	/**
	 * Calculate refund amount for all owned land
	 * @param amountChunks Amount
	 * @return double of amount
	 */
	public double calculateTotalLandRefund(int amountChunks) {
		return calculateTotalLandValue(amountChunks) * Conf.econClaimRefundMultiplier;
	}

	// calculate the cost for claiming land
	public double calculateClaimCost(int amountChunks, boolean takingFromAnotherFaction) {
		if (!shouldBeUsed()) return 0d;

		// basic claim cost, plus land inflation cost, minus the potential bonus given for claiming from another faction
		return Conf.econCostClaimWilderness + (Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * amountChunks) - (takingFromAnotherFaction ? Conf.econCostClaimFromFactionBonus : 0);
	}
	
	public boolean canIControllYou(EconomyParticipator who, EconomyParticipator you) {
		
		// You can deposit to anywhere you feel like. It's your loss if you can't withdraw it again.
		if (who == you) return true;
		
		Faction fWho = RelationUtil.getFaction(who);
		Faction fYou = RelationUtil.getFaction(you);
		
		// This is a system invoker. Accept it.
		if (fWho == null) return true;
		
		if (who instanceof FPlayer) {
			FPlayer fpWho = (FPlayer) who;
			
			// Bypassing players can do any kind of transaction.
			if (fpWho.isAdminBypassing()) return true;
			
			// Players with the any withdraw can do.
			if (Permission.MONEY_WITHDRAW_ANY.has((fpWho.getPlayer()))) return true;
		}

		// A faction can always transfer away the money of it's members and its own money.
		// This will usually not happen as a faction does not have free will, except for things like daily rent to the faction.
		if (who == fWho && fWho == fYou) return true;

		// Factions can be controlled by members that are moderators... or any member if any member can withdraw.
		if (you instanceof Faction && fWho == fYou && (Conf.bankMembersCanWithdraw || ((FPlayer) who).getRole().isAtLeast(Role.MODERATOR))) {
			return true;
		}

		// Can't do this.
		String message = Lang.ECON_LACKSCONTROL.toString();
		message = message.replace("<player>", who.describeTo(who, true));
		message = message.replace("<target>", you.describeTo(who));
		
		who.sendMessage(TextUtil.parseColor(message));
		return false;
	}
	
	public void sendTransferInfo(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount) {
		Set<FPlayer> recipients = new HashSet<FPlayer>();
		recipients.addAll(getFPlayers(invoker));
		recipients.addAll(getFPlayers(from));
		recipients.addAll(getFPlayers(to));

		if (invoker == null) {
			for (FPlayer recipient : recipients) {
				recipient.sendMessage("<h>%s<i> was transferred from <h>%s<i> to <h>%s<i>.", moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		} else if (invoker == from) {
			for (FPlayer recipient : recipients) {
				recipient.sendMessage("<h>%s<i> <h>gave %s<i> to <h>%s<i>.", from.describeTo(recipient, true), moneyString(amount), to.describeTo(recipient));
			}
		} else if (invoker == to) {
			for (FPlayer recipient : recipients) {
				recipient.sendMessage("<h>%s<i> <h>took %s<i> from <h>%s<i>.", to.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient));
			}
		} else {
			for (FPlayer recipient : recipients) {
				recipient.sendMessage("<h>%s<i> transferred <h>%s<i> from <h>%s<i> to <h>%s<i>.", invoker.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		}
	}
	
	public Set<FPlayer> getFPlayers(EconomyParticipator what) {
		Set<FPlayer> fplayers = new HashSet<FPlayer>();

		if (what == null) return fplayers;
		
		if (what instanceof FPlayer) {
			fplayers.add((FPlayer) what);
		} else if (what instanceof Faction) {
			fplayers.addAll(((Faction) what).getMembers());
		}
		
		return fplayers;
	}
	


	public boolean modifyMoney(EconomyParticipator what, double delta, String toDoThis, String forDoingThis) {
		if (!shouldBeUsed()) return false;
		
		OfflinePlayer account = BukkitMixin.getOfflinePlayer(what.getAccountId());
		
		String You = what.describeTo(what, true);

		if (delta == 0) return true;
		
		if (delta > 0) {
			// The player should gain money
			// The account might not have enough space
			EconomyResponse economyResponse = econ.depositPlayer(account, delta);
			if (economyResponse.transactionSuccess()) {
				VaultUtils self = (VaultUtils) this;
				
				self.modifyUniverseMoney(-delta);
				if (forDoingThis != null && !forDoingThis.isEmpty()) {
					what.sendMessage("<h>%s<i> gained <h>%s<i> %s.", You, moneyString(delta), forDoingThis);
				}
				return true;
			} else {
				// transfer to account failed
				if (forDoingThis != null && !forDoingThis.isEmpty()) {
					what.sendMessage("<h>%s<i> would have gained <h>%s<i> %s, but the deposit failed.", You, moneyString(delta), forDoingThis);
				}
				return false;
			}
		} else {
			// The player should loose money
			// The player might not have enough.

			if (econ.has(account, -delta) && econ.withdrawPlayer(account, -delta).transactionSuccess()) {
				// There is enough money to pay
				VaultUtils self = (VaultUtils) this;
				
				self.modifyUniverseMoney(-delta);
				if (forDoingThis != null && !forDoingThis.isEmpty()) {
					what.sendMessage("<h>%s<i> lost <h>%s<i> %s.", You, moneyString(-delta), forDoingThis);
				}
				return true;
			} else {
				// There was not enough money to pay
				if (toDoThis != null && !toDoThis.isEmpty()) {
					what.sendMessage("<h>%s<i> can't afford <h>%s<i> %s.", You, moneyString(-delta), toDoThis);
				}
				return false;
			}
		}
	}
	
	public String getPrimaryGroup(OfflinePlayer player) {
		return this.perms == null || !this.perms.hasGroupSupport() ? " " : this.perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
	}
	
}
