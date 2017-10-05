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

public class CmdFactionsColeader extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsColeader instance = new CmdFactionsColeader();
	public static CmdFactionsColeader get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsColeader() {
		this.aliases.addAll(CommandAliases.cmdAliasesColeader);

		this.requiredArgs.add("player name");

		this.permission = Permission.COLEADER.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = true;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) {
			FancyMessage message = new FancyMessage(Lang.COMMAND_COLEADER_CANDIDATES.toString()).color(ChatColor.GOLD);
			for (FPlayer player : myFaction.getWhereRole(Role.NORMAL)) {
				String name = player.getName();
				message.then(name + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_COLEADER_CLICKTOPROMOTE.toString() + name).command("/" + CommandAliases.baseCommandAliases.get(0) + " coleader " + name);
			}

			this.sendFancyMessage(message);
			return;
		}

		boolean permAny = Permission.COLEADER_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != myFaction && !permAny) {
			this.sendMessage(Lang.COMMAND_COLEADER_NOTMEMBER, you.describeTo(fme, true));
			return;
		}

		if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
			this.sendMessage(Lang.COMMAND_COLEADER_NOTADMIN);
			return;
		}

		if (you == fme && !permAny) {
			this.sendMessage(Lang.COMMAND_COLEADER_SELF);
			return;
		}

		if (you.getRole() == Role.ADMIN) {
			this.sendMessage(Lang.COMMAND_COLEADER_TARGETISADMIN);
			return;
		}

		if (you.getRole() == Role.COLEADER) {
			// Revoke
			you.setRole(Role.NORMAL);
			targetFaction.sendMessage(Lang.COMMAND_COLEADER_REVOKED, you.describeTo(targetFaction, true));
			this.sendMessage(Lang.COMMAND_COLEADER_REVOKES, you.describeTo(fme, true));
		} else {
			// Give
			you.setRole(Role.COLEADER);
			targetFaction.sendMessage(Lang.COMMAND_COLEADER_PROMOTED, you.describeTo(targetFaction, true));
			this.sendMessage(Lang.COMMAND_COLEADER_PROMOTES, you.describeTo(fme, true));
		}
		
		if (Config.rankChangeTitles) {
			targetFaction.getMembers().forEach(fplayer -> {
				String titleHeader = Lang.ROLETITLES_HEADER.getBuilder()
					.parse()
					.replace("<rank>", Role.COLEADER.toNiceName())
					.toString();
				
				String titleFooter = Lang.ROLETITLES_FOOTER.getBuilder()
					.parse()
					.replace("<rank>", Role.COLEADER.toNiceName())
					.replace("<player>", you.describeTo(fplayer))
					.toString();
					
					
				TitleUtil.sendTitle(fplayer.getPlayer(), Config.territoryTitlesTimeFadeInTicks, Config.territoryTitlesTimeStayTicks, Config.territoryTitlesTimeFadeOutTicks, titleHeader, titleFooter);

			});
		}
	}

	@Override
	public boolean isAvailable() {
		return Config.enableColeaders == true && super.isAvailable();
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_COLEADER_DESCRIPTION.toString();
	}

}
