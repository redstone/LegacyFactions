package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;

public class CmdFactionsJoin extends FCommand {

    // -------------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------------- //

    public CmdFactionsJoin() {
        this.aliases.addAll(Conf.cmdAliasesJoin);

        this.requiredArgs.add("faction name");
        this.optionalArgs.put("player", "you");

        this.permission = Permission.JOIN.node;
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
        Faction faction = this.argAsFaction(0);
        if (faction == null) {
            return;
        }

        FPlayer fplayer = this.argAsBestFPlayerMatch(1, fme, false);
        boolean samePlayer = fplayer == fme;

        if (!samePlayer && !Permission.JOIN_OTHERS.has(sender, false)) {
            msg(Lang.COMMAND_JOIN_CANNOTFORCE);
            return;
        }

        if (!faction.isNormal()) {
            msg(Lang.COMMAND_JOIN_SYSTEMFACTION);
            return;
        }

        if (faction == fplayer.getFaction()) {
            //TODO:TL
            msg(Lang.COMMAND_JOIN_ALREADYMEMBER, fplayer.describeTo(fme, true), (samePlayer ? "are" : "is"), faction.getTag(fme));
            return;
        }

        if (Conf.factionMemberLimit > 0 && faction.getFPlayers().size() >= Conf.factionMemberLimit) {
            msg(Lang.COMMAND_JOIN_ATLIMIT, faction.getTag(fme), Conf.factionMemberLimit, fplayer.describeTo(fme, false));
            return;
        }

        if (fplayer.hasFaction()) {
            //TODO:TL
            msg(Lang.COMMAND_JOIN_INOTHERFACTION, fplayer.describeTo(fme, true), (samePlayer ? "your" : "their"));
            return;
        }

        if (!Conf.canLeaveWithNegativePower && fplayer.getPower() < 0) {
            msg(Lang.COMMAND_JOIN_NEGATIVEPOWER, fplayer.describeTo(fme, true));
            return;
        }

        if (!(faction.getOpen() || faction.isInvited(fplayer) || fme.isAdminBypassing() || Permission.JOIN_ANY.has(sender, false))) {
            msg(Lang.COMMAND_JOIN_REQUIRESINVITATION);
            if (samePlayer) {
                faction.msg(Lang.COMMAND_JOIN_ATTEMPTEDJOIN, fplayer.describeTo(faction, true));
            }
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (samePlayer && !canAffordCommand(Conf.econCostJoin, Lang.COMMAND_JOIN_TOJOIN.toString())) {
            return;
        }

        // trigger the join event (cancellable)
        EventFactionsChange event = new EventFactionsChange(fme, fme.getFaction(), faction, true, ChangeReason.COMMAND);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        // then make 'em pay (if applicable)
        if (samePlayer && !payForCommand(Conf.econCostJoin, Lang.COMMAND_JOIN_TOJOIN.toString(), Lang.COMMAND_JOIN_FORJOIN.toString())) {
            return;
        }

        fme.msg(Lang.COMMAND_JOIN_SUCCESS, fplayer.describeTo(fme, true), faction.getTag(fme));

        if (!samePlayer) {
            fplayer.msg(Lang.COMMAND_JOIN_MOVED, fme.describeTo(fplayer, true), faction.getTag(fplayer));
        }
        faction.msg(Lang.COMMAND_JOIN_JOINED, fplayer.describeTo(faction, true));

        fplayer.resetFactionData();
        fplayer.setFaction(faction);
        faction.deinvite(fplayer);

        if (Conf.logFactionJoin) {
            if (samePlayer) {
                Factions.get().log(Lang.COMMAND_JOIN_JOINEDLOG.toString(), fplayer.getName(), faction.getTag());
            } else {
                Factions.get().log(Lang.COMMAND_JOIN_MOVEDLOG.toString(), fme.getName(), fplayer.getName(), faction.getTag());
            }
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_JOIN_DESCRIPTION.toString();
    }
}
