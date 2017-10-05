package net.redstoneore.legacyfactions.cmd;

import java.util.Optional;
import java.util.UUID;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.callback.Callback;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;


public class CmdFactionsOwner extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsOwner instance = new CmdFactionsOwner();
	public static CmdFactionsOwner get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsOwner() {
		this.aliases.addAll(CommandAliases.cmdAliasesOwner);

		this.optionalArgs.put("player name", "you");

		this.permission = Permission.OWNER.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		boolean hasBypass = fme.isAdminBypassing();
		
		if (!hasBypass && !assertHasFaction()) {
			return;
		}

		if (!Config.ownedAreasEnabled) {
			Lang.COMMAND_OWNER_DISABLED.getBuilder()
				.sendToParsed(this.sender);
			return;
		}

		if (!hasBypass && Config.ownedAreasLimitPerFaction > 0 && myFaction.ownership().count() >= Config.ownedAreasLimitPerFaction) {
			this.fme.sendMessage(Lang.COMMAND_OWNER_LIMIT, Config.ownedAreasLimitPerFaction);
			return;
		}

		if (!hasBypass && !assertMinRole(Config.ownedAreasModeratorsCanSet ? Role.MODERATOR : Role.COLEADER)) {
			return;
		}

		final Locality location = Locality.of(this.fme);

		Faction factionHere = Board.get().getFactionAt(Locality.of(me.getLocation()));
		if (factionHere != myFaction) {
			if (!factionHere.isNormal()) {
				Lang.COMMAND_OWNER_NOTCLAIMED.getBuilder()
					.sendToParsed(this.fme);
				return;
			}

			if (!hasBypass) {
				Lang.COMMAND_OWNER_WRONGFACTION.getBuilder()
					.sendToParsed(this.fme);
				return;
			}

		}

		FPlayer target = this.fme;
		if (this.argIsSet(0)) {
			target = this.argAsBestFPlayerMatch(0, null, false);
			
			if (target == null) {
				// Finals for callback
				final FPlayer fplayer = fme;
				final Faction fplayerFaction = myFaction;
				final boolean emptyArgs = args.isEmpty();
				final String playerName = this.argAsString(0);
				
				this.argAsPlayerToMojangUUID(0, null, new Callback<UUID>() {
					@Override
					public void then(UUID result, Optional<Exception> exception) {
						if (exception.isPresent()) {
							exception.get().printStackTrace();
							return;
						}
						
						if (result == null) {
							Lang.COMMAND_ERRORS_PLAYERNOTFOUND.getBuilder()
								.parse()
								.replace("<name>", playerName)
								.sendTo(fme);
							return;
						}
						
						FPlayer targetfplayer = FPlayerColl.get(result);
						if (fplayer == null) {
							Lang.COMMAND_ERRORS_PLAYERNOTFOUND.getBuilder()
								.parse()
								.replace("<name>", playerName)
								.sendTo(fme);
							return;
						}
						
						resume(fplayer, targetfplayer, fplayerFaction, location, emptyArgs);
					}
				});
				
				return;
			}
		}
		
		resume(this.fme, target, this.myFaction, location, args.isEmpty());
	}
	
	private static void resume(FPlayer fme, FPlayer target, Faction myFaction, Locality location, Boolean emptyArgs) {

		String playerName = target.getName();

		if (target.getFaction() != myFaction) {
			fme.sendMessage(Lang.COMMAND_OWNER_NOTMEMBER, playerName);
			return;
		}

		// if no player name was passed, and this claim does already have owners set, clear them
		if (emptyArgs && myFaction.ownership().isOwned(location)) {
			myFaction.ownership().clearAt(location);
			Lang.COMMAND_OWNER_CLEARED.getBuilder()
				.sendToParsed(fme);
			return;
		}

		if (myFaction.ownership().isOwner(location, target)) {
			myFaction.ownership().ownerRemove(location, target);
			fme.sendMessage(Lang.COMMAND_OWNER_REMOVED, playerName);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!fme.payForCommand(Config.econCostOwner, Lang.COMMAND_OWNER_TOSET.toString(), Lang.COMMAND_OWNER_FORSET.toString())) {
			return;
		}

		myFaction.ownership().ownerAdd(location, target);

		fme.sendMessage(Lang.COMMAND_OWNER_ADDED, playerName);
	}

	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_OWNER_DESCRIPTION.toString();
	}
}
