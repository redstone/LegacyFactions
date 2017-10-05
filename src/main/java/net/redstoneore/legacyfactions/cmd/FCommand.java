package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.callback.Callback;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.console.ConsoleFPlayer;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsCommandExecute;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.util.UUIDUtil;
import net.redstoneore.legacyfactions.util.WarmUpUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public abstract class FCommand extends FCommandBase<Factions> {
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Lang.DATE_FORMAT.toString());

	// Due to safety reasons it defaults to disable on lock.
	public boolean disableOnLock = true;

	public boolean senderMustBeMember = false;
	public boolean senderMustBeModerator = false;
	public boolean senderMustBeColeader = false;
	public boolean senderMustBeAdmin = false;

	// The money commands must be disabled if money should not be used.
	public boolean isMoneyCommand = false;
	
	// -------------------------------------------------- //
	// RUNTIME FIELDS
	// -------------------------------------------------- //

	public FPlayer fme;
	public Faction myFaction;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void execute(CommandSender sender, List<String> args, List<FCommandBase<?>> commandChain) {
		if (sender instanceof Player) {
			this.fme = FPlayerColl.get(sender);
			this.myFaction = this.fme.getFaction();
		} else {
			this.fme = ConsoleFPlayer.get();
			this.myFaction = FactionColl.get().getWilderness();
		}
		super.execute(sender, args, commandChain);
	}
	
	@Override
	public final void prePerform() {
		if (EventFactionsCommandExecute.create(this.fme, this).call().isCancelled()) return;
		this.perform();
	}

	@Override
	public boolean isEnabled() {
		// If factions is locked and this command is set to disable on lock
		if (Factions.get().isLocked() && this.disableOnLock) {
			this.sendMessage(Lang.COMMAND_ERRORS_FACTIONSLOCKED.toString());
			return false;
		}
		
		// If this is an economy-only command command and economy is disabled
		if (this.isMoneyCommand && !Config.econEnabled) {
			this.sendMessage(Lang.COMMAND_ERRORS_ECONOMYDISABLED.toString());
			return false;
		}
		
		// If this is an economy-only command command and banks are disabled
		if (this.isMoneyCommand && !Config.bankEnabled) {
			this.sendMessage(Lang.COMMAND_ERRORS_BANKSDISABLED.toString());
			return false;
		}
		
		return true;
	}

	@Override
	public boolean validSenderType(CommandSender sender, boolean informSenderIfNot) {
		// What does the super class say?
		if (!super.validSenderType(sender, informSenderIfNot)) return false;
		
		// if senderMustBeMember, senderMustBeModerator, senderMustBeAdmin, and senderMustBeColeader are not set
		// TODO: [REQUIREMENTS] move to Requirements classes 
		if (!(this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin || this.senderMustBeColeader)) {
			return true;
		}
		
		// If they aren't a player 
		if (!(sender instanceof Player)) return false;

		if (!fme.hasFaction()) {
			sender.sendMessage(TextUtil.get().parse(Lang.COMMAND_ERRORS_NOTMEMBER.toString()));
			return false;
		}

		if (this.senderMustBeModerator && !fme.getRole().isAtLeast(Role.MODERATOR)) {
			String message = TextUtil.get().parse(Lang.COMMAND_ERRORS_ONLYMODERATORSCAN.toString().replaceAll("<theaction>", this.getHelpShort()));

			sender.sendMessage(message);
			return false;
		}

		if (this.senderMustBeColeader && !fme.getRole().isAtLeast(Role.COLEADER)) {
			String message = TextUtil.get().parse(Lang.COMMAND_ERRORS_ONLYCOLEADERSCAN.toString().replaceAll("<theaction>", this.getHelpShort()));

			sender.sendMessage(message);
			return false;
		}

		if (this.senderMustBeAdmin && !fme.getRole().isAtLeast(Role.ADMIN)) {
			String message = TextUtil.get().parse(Lang.COMMAND_ERRORS_ONLYADMINSCAN.toString().replaceAll("<theaction>", this.getHelpShort()));

			sender.sendMessage(message);
			return false;
		}

		return true;
	}

	// -------------------------------------------- //
	// ASSERTIONS
	// -------------------------------------------- //

	public boolean assertHasFaction() {
		if (this.me == null) return true;
		return PlayerMixin.assertHasFaction(this.fme);
	}

	public boolean assertMinRole(Role role) {
		if (this.me == null) return true;
		return PlayerMixin.assertMinRole(this.fme, role, this.getHelpShort());
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
		
		// getUUIDOf will go async and return a sync callback
		UUIDUtil.getUUIDOf(playerName, (uuid, exception) -> {
			if (exception.isPresent()) {
				callback.then(null, exception);
				return;
			}
			
			if (uuid != null) {
				// For sanity sake, set the player name
				FPlayer found = FPlayerColl.get(uuid);
				((SharedFPlayer) found).setName(playerName);
			}
			
			callback.then(uuid, Optional.empty());
		});		
	}
	
	public void argAsPlayerToMojangFPlayer(int idx, FPlayer def, final Callback<FPlayer> callback) {
		final String playerName = this.argAsString(idx);
		
		// getUUIDOf will go async and return a sync callback
		UUIDUtil.getUUIDOf(playerName, (uuid, exception) -> {
			if (exception.isPresent()) {
				callback.then(null, exception);
				return;
			}
			
			// For sanity sake, set the player name
			FPlayer found = FPlayerColl.get(uuid);
			((SharedFPlayer)found).setName(playerName);
			
			callback.then(found, Optional.empty());
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
			Lang.COMMAND_ERRORS_PLAYERORFACTIONNOTFOUND.getBuilder()
				.parse()
				.replace("<name>", name)
				.sendTo(this.sender);
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
	
	public void argAsFactionOrPlayersFaction(int idx, Callback<Faction> callback) {
		Faction faction = (this.argAsFaction(idx, null,false));
		if (faction != null) {
			callback.then(faction, Optional.empty());
			return;
		}
		
		// This callback will be sync 
		this.argAsPlayerToMojangUUID(idx, null, new Callback<UUID>() {
			@Override
			public void then(UUID result, Optional<Exception> exception) {
				if (exception.isPresent()) {
					callback.then(null, exception);
					return;
				}
				if (result == null) {
					callback.then(null, Optional.empty());
					return;
				}
				
				FPlayer fplayer = FPlayerColl.get(result);
				if (fplayer == null) {
					callback.then(null, Optional.empty());
					return;
				}
				callback.then(fplayer.getFaction(), Optional.empty());
			}
		});
	}

	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //



	// if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
	public boolean payForCommand(double cost, String toDoThis, String forDoingThis) {
		if (!VaultEngine.getUtils().shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) {
			return true;
		}

		if (Config.bankEnabled && Config.bankFactionPaysCosts && fme.hasFaction()) {
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

		if (Config.bankEnabled && Config.bankFactionPaysCosts && fme.hasFaction()) {
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
	
	/**
	 * Deprecated use {@link FPlayer#canAdminister}
	 * @param who
	 * @param you
	 * @return
	 */
	@Deprecated
	public boolean canIAdministerYou(FPlayer who, FPlayer you) {
		return who.canAdminister(you);
	}
	
}
