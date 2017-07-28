package net.redstoneore.legacyfactions.integration.vault.util;

import net.redstoneore.legacyfactions.entity.Conf;

@SuppressWarnings("deprecation")
public abstract class VaultUtilNamed extends VaultUtilBase {
	
	/**
	 * Check if a named account exists
	 * @param account name
	 * @return true if the account exists
	 */
	public boolean hasAccount(String name) {
		return this.econ.hasAccount(name);
	}
	
	/**
	 * Get the balance from a named account
	 * @param account name
	 * @return double of balance
	 */
	public double getBalance(String account) {
		return this.econ.getBalance(account);
	}
	
	/**
	 * Set the balance of a named account
	 * @param account name
	 * @param balance amount
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
	 * @param account to deposit to
	 * @param amount to deposit
	 * @return true if success
	 */
	public boolean deposit(String account, double amount) {
		return this.econ.depositPlayer(account, amount).transactionSuccess();
	}
	
	/**
	 * Withdraw an accmount from a named account
	 * @param account to withdraw from
	 * @param amount to withdraw
	 * @return true if success
	 */
	public boolean withdraw(String account, double amount) {
		return this.econ.withdrawPlayer(account, amount).transactionSuccess();
	}
	
	public void modifyUniverseMoney(double delta) {
		if (!this.shouldBeUsed()) return;
		if (Conf.econUniverseAccount == null) return;
		if (Conf.econUniverseAccount.length() == 0) return;
		if (!this.hasAccount(Conf.econUniverseAccount)) return;

		this.modifyBalance(Conf.econUniverseAccount, delta);
	}

}
