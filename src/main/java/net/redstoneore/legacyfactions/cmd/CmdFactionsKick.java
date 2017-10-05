package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;
import net.redstoneore.legacyfactions.lang.Lang;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdFactionsKick extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsKick instance = new CmdFactionsKick();
	public static CmdFactionsKick get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsKick() {
		this.aliases.addAll(CommandAliases.cmdAliasesKick);

		this.optionalArgs.put("player name", "player name");
		//this.optionalArgs.put("", "");

		this.permission = Permission.KICK.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		FPlayer toKick = this.argIsSet(0) ? this.argAsBestFPlayerMatch(0, null, false) : null;
		if (toKick == null) {
			if (this.argIsSet(0)) {
				// Player must be offline
				// Use final here as we don't want to risk how these variables could change when we
				// go off thread, and then back on (argAsPlayerToMojangUUID does this)
				final FPlayer fsender = this.fme;
				final Faction senderFaction = this.myFaction;
				final CommandSender commandSender = this.sender;	 
				final boolean isConsole = this.senderIsConsole;
				final String playerName = this.argAsString(0);
				
				this.argAsPlayerToMojangUUID(0, null, (uuid, exception) -> {
					if (exception.isPresent()) {
						Factions.get().error("Failed to lookup UUID because of an exception");
						exception.get().printStackTrace();					
						return;
					}
					
					if (uuid == null) {
						fsender.sendMessage(Lang.COMMAND_KICK_NONE.toString());
						return;
					}
					
					// Update name in memory as they could potentially not have a name
					FPlayer found = FPlayerColl.get(uuid);
					
					((SharedFPlayer)found).setName(playerName);
					
					// Resume here
					resume(fsender, found, senderFaction, commandSender, isConsole);					
				});
				return;
			}
			
			FancyMessage fancyMessage = new FancyMessage(Lang.COMMAND_KICK_CANDIDATES.toString()).color(ChatColor.GOLD);

			for (FPlayer player : myFaction.getWhereRole(Role.NORMAL)) {
				String name = player.getName();
				fancyMessage.then(name + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_KICK_CLICKTOKICK.toString() + name).command("/" + CommandAliases.baseCommandAliases.get(0) + " " + CommandAliases.cmdAliasesKick.get(0) + " " + name);
			}

			if (fme.getRole().isAtLeast(Role.COLEADER)) {
				for (FPlayer player : myFaction.getWhereRole(Role.MODERATOR)) {
					String s = player.getName();
					fancyMessage.then(s + " ").color(ChatColor.GRAY).tooltip(Lang.COMMAND_KICK_CLICKTOKICK.toString() + s).command("/" + CommandAliases.baseCommandAliases.get(0) + " " + CommandAliases.cmdAliasesKick.get(0) + " " + s);
				}
			}

			if (fme.getRole() == Role.ADMIN) {
				for (FPlayer player : myFaction.getWhereRole(Role.COLEADER)) {
					String s = player.getName();
					fancyMessage.then(s + " ").color(ChatColor.GRAY).tooltip(Lang.COMMAND_KICK_CLICKTOKICK.toString() + s).command("/" + CommandAliases.baseCommandAliases.get(0) + " " + CommandAliases.cmdAliasesKick.get(0) + " " + s);
				}
			}
			this.sendFancyMessage(fancyMessage);
			return;
		}
		
		resume(this.fme, toKick, this.myFaction, this.sender, this.senderIsConsole);
	}
	
	private static void resume(FPlayer fsender, FPlayer toKick, Faction senderFaction, CommandSender commandSender, boolean senderIsConsole) {
		if (fsender == toKick) {
			fsender.sendMessage(Lang.COMMAND_KICK_SELF.toString());
			fsender.sendMessage(Lang.GENERIC_YOUMAYWANT.toString() + CmdFactionsLeave.get().getUseageTemplate(false));
			return;
		}

		Faction toKickFaction = toKick.getFaction();

		if (toKickFaction.isWilderness()) {
			commandSender.sendMessage(Lang.COMMAND_KICK_NONE.toString());
			return;
		}

		// players with admin-level "disband" permission can bypass these requirements
		if (!Permission.KICK_ANY.has(commandSender)) {
			if (toKickFaction != fsender.getFaction()) {
				fsender.sendMessage(Lang.COMMAND_KICK_NOTMEMBER, toKick.describeTo(fsender, true), senderFaction.describeTo(fsender));
				return;
			}

			if (toKick.getRole().isAtLeast(fsender.getRole())) {
				fsender.sendMessage(Lang.COMMAND_KICK_INSUFFICIENTRANK);
				return;
			}

			if (!Config.canLeaveWithNegativePower && toKick.getPower() < 0) {
				fsender.sendMessage(Lang.COMMAND_KICK_NEGATIVEPOWER);
				return;
			}
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if (!fsender.canAffordCommand(Config.econCostKick, Lang.COMMAND_KICK_TOKICK.toString())) {
			return;
		}

		// trigger the leave event (cancellable) [reason:kicked]
		EventFactionsChange event = new EventFactionsChange(toKick, toKick.getFaction(), FactionColl.get().getWilderness(), true, ChangeReason.KICKED);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		// then make 'em pay (if applicable)
		if (!fsender.payForCommand(Config.econCostKick, Lang.COMMAND_KICK_TOKICK.toString(), Lang.COMMAND_KICK_FORKICK.toString())) {
			return;
		}

		toKickFaction.sendMessage(Lang.COMMAND_KICK_FACTION, fsender.describeTo(toKickFaction, true), toKick.describeTo(toKickFaction, true));
		toKick.sendMessage(Lang.COMMAND_KICK_KICKED, fsender.describeTo(toKick, true), toKickFaction.describeTo(toKick));
		if (toKickFaction != senderFaction) {
			fsender.sendMessage(Lang.COMMAND_KICK_KICKS, toKick.describeTo(fsender), toKickFaction.describeTo(fsender));
		}

		if (Config.logFactionKick) {
			// TODO:TL
			Factions.get().log((senderIsConsole ? "A console command" : fsender.getName()) + " kicked " + toKick.getName() + " from the faction: " + toKickFaction.getTag());
		}

		if (toKick.getRole() == Role.ADMIN) {
			toKickFaction.promoteNewLeader();
		}

		toKickFaction.uninvite(toKick);
		toKick.resetFactionData();
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_KICK_DESCRIPTION.toString();
	}

}
