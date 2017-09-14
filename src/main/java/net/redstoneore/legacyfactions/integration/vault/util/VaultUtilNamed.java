package net.redstoneore.legacyfactions.integration.vault.util;

import net.redstoneore.legacyfactions.config.Config;

@SuppressWarnings("deprecation")
public abstract class VaultUtilNamed extends VaultUtilBase {
	
	/**
	 * Check if a named account exists
	 * @param name Account name.
	 * @return true if the account exists
	 */
	public boolean hasAccount(String name) {
		return this.econ.hasAccount(name);
	}
	
	/**
	 * Get the balance from a named account
	 * @param account Account name.
	 * @return Balance.
	 */
	public double getBalance(String account) {
		return this.econ.getBalance(account);
	}
	
	/**
	 * Set the balance of a named account
	 * @param account Account name.
	 * @param amount New balance amount.
	 * @return true if success
	 */
	public boolean setBalance(String account, double amount) {
		double current = this.getBalance(account);
		if (current > amount) {
			return this.econ.withdrawPlayer(account, current - amount).transactionSuccess();
		} else {
			return this.econ.depositPlayer(account, amount - current).transactionSuccess();
		}
	}

	public boolean modifyBalance(String account, double amount) {
		if (amount < 0) {
			return this.econ.withdrawPlayer(account, -amount).transactionSuccess();
		} else {
			return this.econ.depositPlayer(account, amount).transactionSuccess();
		}
	}
	
	/**
	 * Deposit an amount to a named account
	 * @param account Account to deposit to
	 * @param amount Amount to deposit
	 * @return true if success
	 */
	public boolean deposit(String account, double amount) {
		return this.econ.depositPlayer(account, amount).transactionSuccess();
	}
	
	/**
	 * Withdraw an amount from a named account
	 * @param account Account to withdraw from
	 * @param amount Amount to withdraw
	 * @return true if success
	 */
	public boolean withdraw(String account, double amount) {
		return this.econ.withdrawPlayer(account, amount).transactionSuccess();
	}
	
	public void modifyUniverseMoney(double delta) {
		if (!this.shouldBeUsed()) return;
		if (Config.econUniverseAccount == null) return;
		if (Config.econUniverseAccount.length() == 0) return;
		if (!this.hasAccount(Config.econUniverseAccount)) return;

		this.modifyBalance(Config.econUniverseAccount, delta);
	}

}
