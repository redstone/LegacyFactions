package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.callback.Callback;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.util.UUIDUtil;
import net.redstoneore.legacyfactions.util.WarmUpUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public abstract class FCommand extends MCommand<Factions> {
	
	public SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Lang.DATE_FORMAT.toString());

	// Due to safety reasons it defaults to disable on lock.
	public boolean disableOnLock = true;

	public FPlayer fme;
	public Faction myFaction;
	public boolean senderMustBeMember = false;
	public boolean senderMustBeModerator = false;
	public boolean senderMustBeColeader = false;
	public boolean senderMustBeAdmin = false;

	// The money commands must be disabled if money should not be used.
	public boolean isMoneyCommand = false;

	@Override
	public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain) {
		if (sender instanceof Player) {
			this.fme = FPlayerColl.get(sender);
			this.myFaction = this.fme.getFaction();
		} else {
			this.fme = null;
			this.myFaction = null;
		}
		super.execute(sender, args, commandChain);
	}

	@Override
	public boolean isEnabled() {
		if (Factions.get().isLocked() && this.disableOnLock) {
			sendMessage(Lang.COMMAND_ERRORS_FACTIONSLOCKED.toString());
			return false;
		}

		if (this.isMoneyCommand && !Conf.econEnabled) {
			sendMessage(Lang.COMMAND_ERRORS_ECONOMYDISABLED.toString());
			return false;
		}

		if (this.isMoneyCommand && !Conf.bankEnabled) {
			sendMessage(Lang.COMMAND_ERRORS_BANKSDISABLED.toString());
			return false;
		}

		return true;
	}

	@Override
	public boolean validSenderType(CommandSender sender, boolean informSenderIfNot) {
		boolean superValid = super.validSenderType(sender, informSenderIfNot);
		if (!superValid) {
			return false;
		}

		if (!(this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin || this.senderMustBeColeader)) {
			return true;
		}

		if (!(sender instanceof Player)) {
			return false;
		}

		if (!fme.hasFaction()) {
			sender.sendMessage(Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_NOTMEMBER.toString()));
			return false;
		}

		if (this.senderMustBeModerator && !fme.getRole().isAtLeast(Role.MODERATOR)) {
			String message = Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_ONLYMODERATORSCAN.toString().replaceAll("<theaction>", this.getHelpShort()));

			sender.sendMessage(message);
			return false;
		}

		if (this.senderMustBeColeader && !fme.getRole().isAtLeast(Role.COLEADER)) {
			String message = Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_ONLYCOLEADERSCAN.toString().replaceAll("<theaction>", this.getHelpShort()));

			sender.sendMessage(message);
			return false;
		}

		if (this.senderMustBeAdmin && !fme.getRole().isAtLeast(Role.ADMIN)) {
			String message = Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_ONLYADMINSCAN.toString().replaceAll("<theaction>", this.getHelpShort()));

			sender.sendMessage(message);
			return false;
		}

		return true;
	}

	// -------------------------------------------- //
	// Assertions
	// -------------------------------------------- //

	public boolean assertHasFaction() {
		if (me == null) {
			return true;
		}

		if (!fme.hasFaction()) {
			sendMessage(Lang.COMMAND_ERRORS_NOTMEMBER.toString());
			return false;
		}
		return true;
	}

	public boolean assertMinRole(Role role) {
		if (me == null) {
			return true;
		}

		if (fme.getRole().isLessThan(role)) {
			sendMessage(Lang.COMMAND_ERRORS_YOUMUSTBE.toString().replaceAll("<therole>", role.toNiceName()).replaceAll("<theaction>", this.getHelpShort()));
			return false;
		}
		return true;
	}

	// -------------------------------------------- //
	// Argument Readers
	// -------------------------------------------- //

	// FPLAYER ======================
	public FPlayer strAsFPlayer(String name, FPlayer def, boolean msg) {
		FPlayer ret = def;
		
		if (name != null) {
			// Bukkit.getPlayer is case-insensitive and attempts to match
			// so we don't need our own loop for this
			Player player = Bukkit.getPlayer(name);
			if (player != null) {
				String id = player.getUniqueId().toString();
				ret = FPlayerColl.get(id);				
			}				
		}

		if (msg && ret == null) {
			this.sendMessage(Lang.COMMAND_ERRORS_PLAYERNOTFOUND.toString().replace("<name>", name));
		}

		return ret;
	}

	public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg) {
		return this.strAsFPlayer(this.argAsString(idx), def, msg);
	}

	public FPlayer argAsFPlayer(int idx, FPlayer def) {
		return this.argAsFPlayer(idx, def, true);
	}

	public FPlayer argAsFPlayer(int idx) {
		return this.argAsFPlayer(idx, null);
	}

	// BEST FPLAYER MATCH ======================
	public FPlayer strAsBestFPlayerMatch(String name, FPlayer def, boolean msg) {
		return strAsFPlayer(name, def, msg);
	}

	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg) {
		return this.strAsBestFPlayerMatch(this.argAsString(idx), def, msg);
	}

	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def) {
		return this.argAsBestFPlayerMatch(idx, def, true);
	}

	public FPlayer argAsBestFPlayerMatch(int idx) {
		return this.argAsBestFPlayerMatch(idx, null);
	}
	
	// -------------------------------------------------- //
	// UUID
	// -------------------------------------------------- //
	
	public void argAsPlayerToMojangUUID(int idx, UUID def, final Callback<UUID> callback) {
		final String playerName = this.argAsString(idx);
		
		// Okay, lets go async and off the main thread
		UUIDUtil.getUUIDOf(playerName, (uuid, exception) -> {
			if (exception.isPresent()) {
				callback.then(null, exception);
				return;
			}
			
			// For sanity sake, set the player name
			FPlayer found = FPlayerColl.get(uuid);
			found.asMemoryFPlayer().setName(playerName);
			
			callback.then(uuid, Optional.empty());
		});		
	}

	// -------------------------------------------------- //
	// FACTION
	// -------------------------------------------------- //
	
	public Faction strAsFaction(String name, Faction def, boolean msg) {
		Faction ret = def;

		if (name != null) {
			// First we try an exact match
			Faction faction = FactionColl.get().getByTag(name); // Checks for faction name match.

			// Now lets try for warzone / safezone. Helpful for custom warzone / safezone names.
			// Do this after we check for an exact match in case they rename the warzone / safezone
			// and a player created faction took one of the names.
			if (faction == null) {
				if (name.equalsIgnoreCase("warzone")) {
					faction = FactionColl.get().getWarZone();
				} else if (name.equalsIgnoreCase("safezone")) {
					faction = FactionColl.get().getSafeZone();
				}
			}

			// Next we match faction tags
			if (faction == null) {
				faction = FactionColl.get().getBestTagMatch(name);
			}

			// Next we match player names
			if (faction == null) {
				FPlayer fplayer = strAsFPlayer(name, null, false);
				if (fplayer != null) {
					faction = fplayer.getFaction();
				}
			}

			if (faction != null) {
				ret = faction;
			}
		}

		if (msg && ret == null) {
			this.sendMessage(Lang.COMMAND_ERRORS_PLAYERORFACTIONNOTFOUND.toString().replaceAll("<name>", name));
		}

		return ret;
	}

	public Faction argAsFaction(int idx, Faction def, boolean msg) {
		return this.strAsFaction(this.argAsString(idx), def, msg);
	}

	public Faction argAsFaction(int idx, Faction def) {
		return this.argAsFaction(idx, def, true);
	}

	public Faction argAsFaction(int idx) {
		return this.argAsFaction(idx, null);
	}

	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //

	public boolean canIAdministerYou(FPlayer who, FPlayer you) {
		if (!who.getFaction().equals(you.getFaction())) {
			who.sendMessage(Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_NOTSAME.toString().replaceAll("<name>", you.describeTo(who, true))));
			return false;
		}

		if (who.getRole().isMoreThan(you.getRole()) || who.getRole().equals(Role.ADMIN)) {
			return true;
		}

		if (you.getRole().equals(Role.ADMIN)) {
			who.sendMessage(Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_ONLYFACTIONADMIN.toString()));
		} else if (who.getRole().equals(Role.MODERATOR)) {
			if (who == you) return true;

			who.sendMessage(Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_MODERATORSCANT.toString()));
		} else if (who.getRole().equals(Role.COLEADER)) {
			if (who == you) return true;

			who.sendMessage(Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_COLEADERSCANT.toString()));
		} else {
			who.sendMessage(Factions.get().getTextUtil().parse(Lang.COMMAND_ERRORS_NOTMODERATOR.toString()));
		}

		return false;
	}

	// if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
	public boolean payForCommand(double cost, String toDoThis, String forDoingThis) {
		if (!VaultEngine.getUtils().shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) {
			return true;
		}

		if (Conf.bankEnabled && Conf.bankFactionPaysCosts && fme.hasFaction()) {
			return VaultEngine.getUtils().modifyMoney(myFaction, -cost, toDoThis, forDoingThis);
		} else {
			return VaultEngine.getUtils().modifyMoney(fme, -cost, toDoThis, forDoingThis);
		}
	}

	public boolean payForCommand(double cost, Lang toDoThis, Lang forDoingThis) {
		return payForCommand(cost, toDoThis.toString(), forDoingThis.toString());
	}

	// like above, but just make sure they can pay; returns true unless person can't afford the cost
	public boolean canAffordCommand(double cost, String toDoThis) {
		if (!VaultEngine.getUtils().shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) {
			return true;
		}

		if (Conf.bankEnabled && Conf.bankFactionPaysCosts && fme.hasFaction()) {
			return VaultEngine.getUtils().hasAtLeast(myFaction, cost, toDoThis);
		} else {
			return VaultEngine.getUtils().hasAtLeast(fme, cost, toDoThis);
		}
	}

	public void doWarmUp(WarmUpUtil.Warmup warmup, Lang translationKey, String action, Runnable runnable, long delay) {
		this.doWarmUp(this.fme, warmup, translationKey, action, runnable, delay);
	}

	public void doWarmUp(FPlayer player, WarmUpUtil.Warmup warmup, Lang translationKey, String action, Runnable runnable, long delay) {
		WarmUpUtil.process(player, warmup, translationKey, action, runnable, delay);
	}
}
