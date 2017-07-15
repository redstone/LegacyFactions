package net.redstoneore.legacyfactions.integration.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.cmd.CmdFactions;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.integration.IntegrationEngine;
import net.redstoneore.legacyfactions.util.RelationUtil;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class VaultEngine extends IntegrationEngine {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

	protected static Economy econ = null;
	protected static net.milkbowl.vault.permission.Permission perms = null;
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static void setup() {
		if (isSetup()) return;

		try {
			RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> rsp = Factions.get().getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			if (rsp != null) {
				perms = rsp.getProvider();
			}
		} catch (NoClassDefFoundError ex) {
			// Fail silently.
		}
				
		String integrationFail = "Economy integration is " + (Conf.econEnabled ? "enabled, but" : "disabled, and") + " the plugin \"Vault\" ";

		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			Factions.get().log(integrationFail + "is not installed.");
			return;
		}

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			Factions.get().log(integrationFail + "is not hooked into an economy plugin.");
			return;
		}
		econ = rsp.getProvider();

		Factions.get().log("Economy integration through Vault plugin successful.");

		if (!Conf.econEnabled) {
			Factions.get().log("NOTE: Economy is disabled. You can enable it with the command: f config econEnabled true");
		}

		CmdFactions.get().cmdHelp.updateHelp();
	}
	
	public static boolean shouldBeUsed() {
		return Conf.econEnabled && econ != null && econ.isEnabled();
	}
	
	public static boolean isSetup() {
		return econ != null;
	}

	public static void modifyUniverseMoney(double delta) {
		if (!shouldBeUsed()) return;

		if (Conf.econUniverseAccount == null) return;
		
		if (Conf.econUniverseAccount.length() == 0) return;
		
		if (!econ.hasAccount(Conf.econUniverseAccount)) return;

		modifyBalance(Conf.econUniverseAccount, delta);
	}

	public static void sendBalanceInfo(FPlayer to, EconomyParticipator about) {
		if (!shouldBeUsed()) {
			Factions.get().warn("Vault does not appear to be hooked into an economy plugin.");
			return;
		}
		
		to.msg("<a>%s's<i> balance is <h>%s<i>.", about.describeTo(to, true), VaultEngine.moneyString(econ.getBalance(about.getAccountId())));
	}

	public static boolean canIControllYou(EconomyParticipator who, EconomyParticipator you) {
		
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
		// TODO: Lang
		who.msg("<h>%s<i> lacks permission to control <h>%s's<i> money.", who.describeTo(who, true), you.describeTo(who));
		return false;
	}

	public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount) {
		return transferMoney(invoker, from, to, amount, true);
	}

	public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount, boolean notify) {
		if (!shouldBeUsed()) return false;

		// The amount must be positive.
		// If the amount is negative we must flip and multiply amount with -1.
		if (amount < 0) {
			amount *= -1;
			EconomyParticipator temp = from;
			from = to;
			to = temp;
		}

		// Check the rights
		if (!canIControllYou(invoker, from)) return false;

		OfflinePlayer fromAccount;
		OfflinePlayer toAccount;

		if (isUUID(from.getAccountId())) {
			fromAccount = Bukkit.getOfflinePlayer(UUID.fromString(from.getAccountId()));
			if (fromAccount.getName() == null) return false;
		} else {
			fromAccount = Bukkit.getOfflinePlayer(from.getAccountId());
		}

		if (isUUID(to.getAccountId())) {
			toAccount = Bukkit.getOfflinePlayer(UUID.fromString(to.getAccountId()));
			if (toAccount.getName() == null) return false;
		} else {
			toAccount = Bukkit.getOfflinePlayer(to.getAccountId());
		}

		// Is there enough money for the transaction to happen?
		if (!econ.has(fromAccount.getName(), amount)) {
			// There was not enough money to pay
			if (invoker != null && notify) {
				invoker.msg("<h>%s<b> can't afford to transfer <h>%s<b> to %s<b>.", from.describeTo(invoker, true), moneyString(amount), to.describeTo(invoker));
			}

			return false;
		}

		// Transfer money
		EconomyResponse economyResponseWithdraw = econ.withdrawPlayer(fromAccount.getName(), amount);

		if (economyResponseWithdraw.transactionSuccess()) {
			EconomyResponse economyResponseDeposit = econ.depositPlayer(toAccount.getName(), amount);
			if (economyResponseDeposit.transactionSuccess()) {
				if (notify) {
					sendTransferInfo(invoker, from, to, amount);
				}
				return true;
			} else {
				// transaction failed, refund account
				econ.depositPlayer(fromAccount.getName(), amount);
			}
		}

		// if we get here something with the transaction failed
		if (notify) {
			invoker.msg("Unable to transfer %s<b> to <h>%s<b> from <h>%s<b>.", moneyString(amount), to.describeTo(invoker), from.describeTo(invoker, true));
		}

		return false;
	}

	public static Set<FPlayer> getFplayers(EconomyParticipator what) {
		Set<FPlayer> fplayers = new HashSet<FPlayer>();

		if (what == null) return fplayers;
		
		if (what instanceof FPlayer) {
			fplayers.add((FPlayer) what);
		} else if (what instanceof Faction) {
			fplayers.addAll(((Faction) what).getFPlayers());
		}
		
		return fplayers;
	}

	public static void sendTransferInfo(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount) {
		Set<FPlayer> recipients = new HashSet<FPlayer>();
		recipients.addAll(getFplayers(invoker));
		recipients.addAll(getFplayers(from));
		recipients.addAll(getFplayers(to));

		if (invoker == null) {
			for (FPlayer recipient : recipients) {
				recipient.msg("<h>%s<i> was transferred from <h>%s<i> to <h>%s<i>.", moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		} else if (invoker == from) {
			for (FPlayer recipient : recipients) {
				recipient.msg("<h>%s<i> <h>gave %s<i> to <h>%s<i>.", from.describeTo(recipient, true), moneyString(amount), to.describeTo(recipient));
			}
		} else if (invoker == to) {
			for (FPlayer recipient : recipients) {
				recipient.msg("<h>%s<i> <h>took %s<i> from <h>%s<i>.", to.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient));
			}
		} else {
			for (FPlayer recipient : recipients) {
				recipient.msg("<h>%s<i> transferred <h>%s<i> from <h>%s<i> to <h>%s<i>.", invoker.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		}
	}

	public static boolean hasAtLeast(EconomyParticipator what, double delta, String toDoThis) {
		if (!shouldBeUsed()) return true;

		// going the hard way round as econ.has refuses to work.
		boolean affordable = false;
		double currentBalance;

		if (isUUID(what.getAccountId())) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(what.getAccountId()));
			if (offline.getName() != null) {
				currentBalance = econ.getBalance(offline.getName());
			} else {
				currentBalance = 0;
			}
		} else {
			currentBalance = econ.getBalance(what.getAccountId());
		}

		if (currentBalance >= delta) {
			affordable = true;
		}

		if (!affordable) {
			if (toDoThis != null && !toDoThis.isEmpty()) {
				what.msg("<h>%s<i> can't afford <h>%s<i> %s.", what.describeTo(what, true), moneyString(delta), toDoThis);
			}
			return false;
		}
		return true;
	}

	public static boolean modifyMoney(EconomyParticipator what, double delta, String toDoThis, String forDoingThis) {
		if (!shouldBeUsed()) return false;

		OfflinePlayer account;

		if (isUUID(what.getAccountId())) {
			account = Bukkit.getOfflinePlayer(UUID.fromString(what.getAccountId()));
			if (account.getName() == null) {
				return false;
			}
		} else {
			account = Bukkit.getOfflinePlayer(what.getAccountId());
		}

		String You = what.describeTo(what, true);

		if (delta == 0) return true;
		
		if (delta > 0) {
			// The player should gain money
			// The account might not have enough space
			EconomyResponse er = econ.depositPlayer(account.getName(), delta);
			if (er.transactionSuccess()) {
				modifyUniverseMoney(-delta);
				if (forDoingThis != null && !forDoingThis.isEmpty()) {
					what.msg("<h>%s<i> gained <h>%s<i> %s.", You, moneyString(delta), forDoingThis);
				}
				return true;
			} else {
				// transfer to account failed
				if (forDoingThis != null && !forDoingThis.isEmpty()) {
					what.msg("<h>%s<i> would have gained <h>%s<i> %s, but the deposit failed.", You, moneyString(delta), forDoingThis);
				}
				return false;
			}
		} else {
			// The player should loose money
			// The player might not have enough.

			if (econ.has(account.getName(), -delta) && econ.withdrawPlayer(account.getName(), -delta).transactionSuccess()) {
				// There is enough money to pay
				modifyUniverseMoney(-delta);
				if (forDoingThis != null && !forDoingThis.isEmpty()) {
					what.msg("<h>%s<i> lost <h>%s<i> %s.", You, moneyString(-delta), forDoingThis);
				}
				return true;
			} else {
				// There was not enough money to pay
				if (toDoThis != null && !toDoThis.isEmpty()) {
					what.msg("<h>%s<i> can't afford <h>%s<i> %s.", You, moneyString(-delta), toDoThis);
				}
				return false;
			}
		}
	}

	// format money string based on server's set currency type, like "24 gold" or "$24.50"
	public static String moneyString(double amount) {
		return econ.format(amount);
	}

	// calculate the cost for claiming land
	public static double calculateClaimCost(int ownedLand, boolean takingFromAnotherFaction) {
		if (!shouldBeUsed()) {
			return 0d;
		}

		// basic claim cost, plus land inflation cost, minus the potential bonus given for claiming from another faction
		return Conf.econCostClaimWilderness + (Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * ownedLand) - (takingFromAnotherFaction ? Conf.econCostClaimFromFactionBonus : 0);
	}

	// calculate refund amount for unclaiming land
	public static double calculateClaimRefund(int ownedLand) {
		return calculateClaimCost(ownedLand - 1, false) * Conf.econClaimRefundMultiplier;
	}

	// calculate value of all owned land
	public static double calculateTotalLandValue(int ownedLand) {
		double amount = 0;
		for (int x = 0; x < ownedLand; x++) {
			amount += calculateClaimCost(x, false);
		}
		return amount;
	}

	// calculate refund amount for all owned land
	public static double calculateTotalLandRefund(int ownedLand) {
		return calculateTotalLandValue(ownedLand) * Conf.econClaimRefundMultiplier;
	}


	// -------------------------------------------------- //
	// Standard account management methods
	// -------------------------------------------------- //

	public static boolean hasAccount(String name) {
		return econ.hasAccount(name);
	}
	
	public static double getBalance(String account) {
		return econ.getBalance(account);
	}

	private static final DecimalFormat format = new DecimalFormat("#,###");

	public static String getFriendlyBalance(UUID uuid) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
		if (offline.getName() == null) {
			return "0";
		}
		return format.format(econ.getBalance(offline.getName()));
	}

	public static String getFriendlyBalance(FPlayer player) {
		return getFriendlyBalance(UUID.fromString(player.getId()));
	}

	public static boolean setBalance(String account, double amount) {
		double current = econ.getBalance(account);
		if (current > amount) {
			return econ.withdrawPlayer(account, current - amount).transactionSuccess();
		} else {
			return econ.depositPlayer(account, amount - current).transactionSuccess();
		}
	}

	public static boolean modifyBalance(String account, double amount) {
		if (amount < 0) {
			return econ.withdrawPlayer(account, -amount).transactionSuccess();
		} else {
			return econ.depositPlayer(account, amount).transactionSuccess();
		}
	}

	public static boolean deposit(String account, double amount) {
		return econ.depositPlayer(account, amount).transactionSuccess();
	}

	public static boolean withdraw(String account, double amount) {
		return econ.withdrawPlayer(account, amount).transactionSuccess();
	}

	// -------------------------------------------------- //
	// UTIL
	// -------------------------------------------------- //

	public static boolean isUUID(String uuid) {
		try {
			UUID.fromString(uuid);
		} catch (IllegalArgumentException ex) {
			return false;
		}

		return true;
	}
}
