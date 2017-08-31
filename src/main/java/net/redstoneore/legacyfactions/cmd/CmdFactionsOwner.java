package net.redstoneore.legacyfactions.cmd;

import java.util.Optional;
import java.util.UUID;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.callback.Callback;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.CommandAliases;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
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

	// TODO: Fix colors!

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		boolean hasBypass = fme.isAdminBypassing();
		
		if (!hasBypass && !assertHasFaction()) {
			return;
		}

		if (!Conf.ownedAreasEnabled) {
			this.sendMessage(Lang.COMMAND_OWNER_DISABLED);
			return;
		}

		if (!hasBypass && Conf.ownedAreasLimitPerFaction > 0 && myFaction.getCountOfClaimsWithOwners() >= Conf.ownedAreasLimitPerFaction) {
			this.fme.sendMessage(Lang.COMMAND_OWNER_LIMIT, Conf.ownedAreasLimitPerFaction);
			return;
		}

		if (!hasBypass && !assertMinRole(Conf.ownedAreasModeratorsCanSet ? Role.MODERATOR : Role.COLEADER)) {
			return;
		}

		final FLocation flocation = new FLocation(fme);

		Faction factionHere = Board.get().getFactionAt(Locality.of(me.getLocation()));
		if (factionHere != myFaction) {
			if (!factionHere.isNormal()) {
				fme.sendMessage(Lang.COMMAND_OWNER_NOTCLAIMED);
				return;
			}

			if (!hasBypass) {
				fme.sendMessage(Lang.COMMAND_OWNER_WRONGFACTION);
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
							fme.sendMessage(Lang.COMMAND_ERRORS_PLAYERNOTFOUND.toString().replace("<name>", playerName));
							return;
						}
						
						FPlayer targetfplayer = FPlayerColl.get(result);
						if (fplayer == null) {
							fme.sendMessage(Lang.COMMAND_ERRORS_PLAYERNOTFOUND.toString().replace("<name>", playerName));
							return;
						}
						
						resume(fplayer, targetfplayer, fplayerFaction, flocation, emptyArgs);
					}
				});
				
				return;
			}
		}
		
		resume(this.fme, target, this.myFaction, flocation, args.isEmpty());
	}
	
	private static void resume(FPlayer fme, FPlayer target, Faction myFaction, FLocation flocation, Boolean emptyArgs) {

		String playerName = target.getName();

		if (target.getFaction() != myFaction) {
			fme.sendMessage(Lang.COMMAND_OWNER_NOTMEMBER, playerName);
			return;
		}

		// if no player name was passed, and this claim does already have owners set, clear them
		if (emptyArgs && myFaction.doesLocationHaveOwnersSet(flocation)) {
			myFaction.clearClaimOwnership(flocation);
			fme.sendMessage(Lang.COMMAND_OWNER_CLEARED);
			return;
		}

		if (myFaction.isPlayerInOwnerList(target, flocation)) {
			myFaction.removePlayerAsOwner(target, flocation);
			fme.sendMessage(Lang.COMMAND_OWNER_REMOVED, playerName);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!fme.payForCommand(Conf.econCostOwner, Lang.COMMAND_OWNER_TOSET.toString(), Lang.COMMAND_OWNER_FORSET.toString())) {
			return;
		}

		myFaction.setPlayerAsOwner(target, flocation);

		fme.sendMessage(Lang.COMMAND_OWNER_ADDED, playerName);
	}

	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_OWNER_DESCRIPTION.toString();
	}
}
