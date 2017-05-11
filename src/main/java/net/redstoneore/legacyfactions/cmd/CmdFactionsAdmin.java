package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;

public class CmdFactionsAdmin extends FCommand {
	
	// -------------------------------------------------- //
	// SINGLETON
	// -------------------------------------------------- //
	
	private static CmdFactionsAdmin i = new CmdFactionsAdmin();
	public static CmdFactionsAdmin get() { return i; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsAdmin() {
		this.aliases.addAll(Conf.cmdAliasesAdmin);
		
		this.requiredArgs.add("player name");
		
		this.permission = Permission.ADMIN.node;
		this.disableOnLock = true;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
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
			msg(Lang.COMMAND_ADMIN_NOTMEMBER, newAdmin.describeTo(fme, true));
			return;
		}
		
		// Am I an admin?
		if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
			msg(Lang.COMMAND_ADMIN_NOTADMIN);
			return;
		}

		// Check for self
		if (newAdmin == fme && !permAny) {
			msg(Lang.COMMAND_ADMIN_TARGETSELF);
			return;
		}

		// Only call a EventFactionsChange when newLeader isn't actually in the faction
		if (newAdmin.getFaction() != targetFaction) {
			EventFactionsChange event = new EventFactionsChange(this.fme, this.fme.getFaction(), targetFaction, true, ChangeReason.LEADER);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) return;
		}

		FPlayer admin = targetFaction.getFPlayerAdmin();

		// if target player is currently admin, demote and replace him
		if (newAdmin == admin) {
			targetFaction.promoteNewLeader();
			this.msg(Lang.COMMAND_ADMIN_DEMOTES, newAdmin.describeTo(fme, true));
			newAdmin.msg(Lang.COMMAND_ADMIN_DEMOTED, senderIsConsole ? Lang.GENERIC_SERVERADMIN.toString() : fme.describeTo(newAdmin, true));
			return;
		}

		// Demote existing admin if one exists
		if (admin != null) {
			admin.setRole(Role.MODERATOR);
		}
		
		// Promote new admin
		newAdmin.setRole(Role.ADMIN);
		
		this.msg(Lang.COMMAND_ADMIN_PROMOTES, newAdmin.describeTo(fme, true));

		// Inform all players
		FPlayerColl.all(true, fplayer -> 
			fplayer.msg(Lang.COMMAND_ADMIN_PROMOTED, senderIsConsole ? Lang.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer, true), newAdmin.describeTo(fplayer), targetFaction.describeTo(fplayer))
		);
	}

	public String getUsageTranslation() {
		return Lang.COMMAND_ADMIN_DESCRIPTION.toString();
	}

}
