package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

import org.bukkit.ChatColor;

public class CmdFactionsColeader extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

    public CmdFactionsColeader() {
        this.aliases.addAll(Conf.cmdAliasesColeader);

        this.requiredArgs.add("player name");

        this.permission = Permission.COLEADER.node;
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
            for (FPlayer player : myFaction.getFPlayersWhereRole(Role.NORMAL)) {
                String name = player.getName();
				message.then(name + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_COLEADER_CLICKTOPROMOTE.toString() + name).command("/" + Conf.baseCommandAliases.get(0) + " coleader " + name);
            }

            this.sendFancyMessage(message);
            return;
        }

        boolean permAny = Permission.COLEADER_ANY.has(sender, false);
        Faction targetFaction = you.getFaction();

        if (targetFaction != myFaction && !permAny) {
            this.msg(Lang.COMMAND_COLEADER_NOTMEMBER, you.describeTo(fme, true));
            return;
        }

        if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
			this. msg(Lang.COMMAND_COLEADER_NOTADMIN);
            return;
        }

        if (you == fme && !permAny) {
			this. msg(Lang.COMMAND_COLEADER_SELF);
            return;
        }

        if (you.getRole() == Role.ADMIN) {
			this.msg(Lang.COMMAND_COLEADER_TARGETISADMIN);
            return;
        }

        if (you.getRole() == Role.COLEADER) {
            // Revoke
            you.setRole(Role.NORMAL);
            targetFaction.msg(Lang.COMMAND_COLEADER_REVOKED, you.describeTo(targetFaction, true));
			this.msg(Lang.COMMAND_COLEADER_REVOKES, you.describeTo(fme, true));
        } else {
            // Give
            you.setRole(Role.COLEADER);
            targetFaction.msg(Lang.COMMAND_COLEADER_PROMOTED, you.describeTo(targetFaction, true));
			this.msg(Lang.COMMAND_COLEADER_PROMOTES, you.describeTo(fme, true));
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_COLEADER_DESCRIPTION.toString();
    }

}
