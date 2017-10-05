package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TitleUtil;

import org.bukkit.ChatColor;

public class CmdFactionsMod extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsMod instance = new CmdFactionsMod();
	public static CmdFactionsMod get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsMod() {
		this.aliases.addAll(CommandAliases.cmdAliasesMod);

		this.requiredArgs.add("player name");
		
		this.permission = Permission.MOD.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = true;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) {
			FancyMessage msg = new FancyMessage(Lang.COMMAND_MOD_CANDIDATES.toString()).color(ChatColor.GOLD);
			for (FPlayer player : myFaction.getWhereRole(Role.NORMAL)) {
				String s = player.getName();
				msg.then(s + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_MOD_CLICKTOPROMOTE.toString() + s).command("/" + CommandAliases.baseCommandAliases.get(0) + " mod " + s);
			}

			sendFancyMessage(msg);
			return;
		}

		boolean permAny = Permission.MOD_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != myFaction && !permAny) {
			sendMessage(Lang.COMMAND_MOD_NOTMEMBER, you.describeTo(fme, true));
			return;
		}

		if (fme != null && !fme.getRole().isAtLeast(Role.COLEADER) && !permAny) {
			sendMessage(Lang.COMMAND_MOD_NOTADMIN);
			return;
		}

		if (you == fme && !permAny) {
			sendMessage(Lang.COMMAND_MOD_SELF);
			return;
		}

		if (you.getRole() == Role.ADMIN) {
			sendMessage(Lang.COMMAND_MOD_TARGETISADMIN);
			return;
		}

		if (you.getRole() == Role.MODERATOR) {
			// Revoke
			you.setRole(Role.NORMAL);
			targetFaction.sendMessage(Lang.COMMAND_MOD_REVOKED, you.describeTo(targetFaction, true));
			sendMessage(Lang.COMMAND_MOD_REVOKES, you.describeTo(fme, true));
		} else {
			// Give
			you.setRole(Role.MODERATOR);
			targetFaction.sendMessage(Lang.COMMAND_MOD_PROMOTED, you.describeTo(targetFaction, true));
			sendMessage(Lang.COMMAND_MOD_PROMOTES, you.describeTo(fme, true));
		}
		
		if (Config.rankChangeTitles) {
			targetFaction.getMembers().forEach(fplayer -> {
				String titleHeader = Lang.ROLETITLES_HEADER.getBuilder()
					.parse()
					.replace("<rank>", Role.MODERATOR.toNiceName())
					.toString();
				
				String titleFooter = Lang.ROLETITLES_FOOTER.getBuilder()
					.parse()
					.replace("<rank>", Role.MODERATOR.toNiceName())
					.replace("<player>", you.describeTo(fplayer))
					.toString();
					
					
				TitleUtil.sendTitle(fplayer.getPlayer(), Config.territoryTitlesTimeFadeInTicks, Config.territoryTitlesTimeStayTicks, Config.territoryTitlesTimeFadeOutTicks, titleHeader, titleFooter);

			});
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_MOD_DESCRIPTION.toString();
	}

}
