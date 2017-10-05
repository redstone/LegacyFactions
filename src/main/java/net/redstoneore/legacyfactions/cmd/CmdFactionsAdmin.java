package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TitleUtil;

public class CmdFactionsAdmin extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsAdmin instance = new CmdFactionsAdmin();
	public static CmdFactionsAdmin get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsAdmin() {
		this.aliases.addAll(CommandAliases.cmdAliasesAdmin);
		
		this.requiredArgs.add("player name");
		
		this.permission = Permission.ADMIN.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = false;
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
		FPlayer newAdmin = this.argAsBestFPlayerMatch(0);
		if (newAdmin == null) return;

		Boolean permAny = Permission.ADMIN_ANY.has(sender, false);
		Faction targetFaction = newAdmin.getFaction();

		// Must be a member
		if (targetFaction != myFaction && !permAny) {
			sendMessage(Lang.COMMAND_ADMIN_NOTMEMBER.getBuilder().parse().toString(), newAdmin.describeTo(fme, true));
			return;
		}
		
		// Am I an admin?
		if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
			sendMessage(Lang.COMMAND_ADMIN_NOTADMIN.getBuilder().parse().toString());
			return;
		}

		// Check for self
		if (newAdmin == fme && !permAny) {
			sendMessage(Lang.COMMAND_ADMIN_TARGETSELF.getBuilder().parse().toString());
			return;
		}

		// Only call a EventFactionsChange when newLeader isn't actually in the faction
		if (newAdmin.getFaction() != targetFaction) {
			EventFactionsChange event = new EventFactionsChange(this.fme, this.fme.getFaction(), targetFaction, true, ChangeReason.LEADER);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) return;
		}

		FPlayer admin = targetFaction.getOwner();

		// if target player is currently admin, demote and replace him
		if (newAdmin == admin) {
			targetFaction.promoteNewLeader();
			this.sendMessage(Lang.COMMAND_ADMIN_DEMOTES.getBuilder().parse().toString(), newAdmin.describeTo(fme, true));
			newAdmin.sendMessage(Lang.COMMAND_ADMIN_DEMOTED.getBuilder().parse().toString(), senderIsConsole ? Lang.GENERIC_SERVERADMIN.getBuilder().parse().toString() : fme.describeTo(newAdmin, true));
			return;
		}

		// Demote existing admin if one exists
		if (admin != null) {
			admin.setRole(Role.MODERATOR);
		}
		
		// Promote new admin
		newAdmin.setRole(Role.ADMIN);
		
		this.sendMessage(Lang.COMMAND_ADMIN_PROMOTES.getBuilder().parse().toString(), newAdmin.describeTo(fme, true));

		// Inform all players
		FPlayerColl.all(true, fplayer -> 
			fplayer.sendMessage(Lang.COMMAND_ADMIN_PROMOTED.getBuilder().parse().toString(), senderIsConsole ? Lang.GENERIC_SERVERADMIN.getBuilder().parse().toString() : fme.describeTo(fplayer, true), newAdmin.describeTo(fplayer), targetFaction.describeTo(fplayer))
		);
		
		if (Config.rankChangeTitles) {
			targetFaction.getMembers().forEach(fplayer -> {
				String titleHeader = Lang.ROLETITLES_HEADER.getBuilder()
					.parse()
					.replace("<rank>", Role.ADMIN.toNiceName())
					.toString();
				
				String titleFooter = Lang.ROLETITLES_FOOTER.getBuilder()
					.parse()
					.replace("<rank>", Role.ADMIN.toNiceName())
					.replace("<player>", newAdmin.describeTo(fplayer))
					.toString();
					
					
				TitleUtil.sendTitle(fplayer.getPlayer(), Config.territoryTitlesTimeFadeInTicks, Config.territoryTitlesTimeStayTicks, Config.territoryTitlesTimeFadeOutTicks, titleHeader, titleFooter);

			});
		}
	}

	public String getUsageTranslation() {
		return Lang.COMMAND_ADMIN_DESCRIPTION.getBuilder().parse().toString();
	}

}
