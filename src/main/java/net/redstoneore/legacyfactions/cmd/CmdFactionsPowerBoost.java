package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.CommandAliases;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

public class CmdFactionsPowerBoost extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsPowerBoost instance = new CmdFactionsPowerBoost();
	public static CmdFactionsPowerBoost get() { return instance; }
	
    // -------------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------------- //

    private CmdFactionsPowerBoost() {
        this.aliases.addAll(CommandAliases.cmdAliasesPowerBoost);

        this.requiredArgs.add("p|f|player|faction");
        this.requiredArgs.add("name");
        this.requiredArgs.add("# or reset");

        this.permission = Permission.POWERBOOST.getNode();
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
        String type = this.argAsString(0).toLowerCase();
        boolean doPlayer = true;
        if (type.equals("f") || type.equals("faction")) {
            doPlayer = false;
        } else if (!type.equals("p") && !type.equals("player")) {
            sendMessage(Lang.COMMAND_POWERBOOST_HELP_1);
            sendMessage(Lang.COMMAND_POWERBOOST_HELP_2);
            return;
        }

        Double targetPower = this.argAsDouble(2);
        if (targetPower == null) {
            if (this.argAsString(2).equalsIgnoreCase("reset")) {
                targetPower = 0D;
            } else {
                sendMessage(Lang.COMMAND_POWERBOOST_INVALIDNUM);
                return;
            }
        }

        String target;

        if (doPlayer) {
            FPlayer targetPlayer = this.argAsBestFPlayerMatch(1);
            if (targetPlayer == null) {
                return;
            }

            if (targetPower != 0) {
                targetPower += targetPlayer.getPowerBoost();
            }
            targetPlayer.setPowerBoost(targetPower);
            target = Lang.COMMAND_POWERBOOST_PLAYER.format(targetPlayer.getName());
        } else {
            Faction targetFaction = this.argAsFaction(1);
            if (targetFaction == null) {
                return;
            }

            if (targetPower != 0) {
                targetPower += targetFaction.getPowerBoost();
            }
            targetFaction.setPowerBoost(targetPower);
            target = Lang.COMMAND_POWERBOOST_FACTION.format(targetFaction.getTag());
        }

        int roundedPower = (int) Math.round(targetPower);
        sendMessage(Lang.COMMAND_POWERBOOST_BOOST, target, roundedPower);
        if (!senderIsConsole) {
            Factions.get().log(Lang.COMMAND_POWERBOOST_BOOSTLOG.toString(), fme.getName(), target, roundedPower);
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_POWERBOOST_DESCRIPTION.toString();
    }
}
