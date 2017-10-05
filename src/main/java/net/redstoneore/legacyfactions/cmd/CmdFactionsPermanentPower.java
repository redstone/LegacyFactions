package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsPermanentPower extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsPermanentPower instance = new CmdFactionsPermanentPower();
	public static CmdFactionsPermanentPower get() { return instance; }
	
    // -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

    private CmdFactionsPermanentPower() {
        this.aliases.addAll(CommandAliases.cmdAliasesPermanentPower);

        this.requiredArgs.add("faction");
        this.optionalArgs.put("power", "reset");

        this.permission = Permission.SET_PERMANENTPOWER.getNode();
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
        Faction targetFaction = this.argAsFaction(0);
        if (targetFaction == null) {
            return;
        }

        Integer targetPower = this.argAsInt(1);

        targetFaction.setPermanentPower(targetPower);

        String change = Lang.COMMAND_PERMANENTPOWER_REVOKE.toString();
        if (targetFaction.hasPermanentPower()) {
            change = Lang.COMMAND_PERMANENTPOWER_GRANT.toString();
        }

        // Inform sender
        sendMessage(Lang.COMMAND_PERMANENTPOWER_SUCCESS, change, targetFaction.describeTo(fme));

        // Inform all other players
        for (FPlayer fplayer : targetFaction.getWhereOnline(true)) {
            if (fplayer == fme) {
                continue;
            }
            String blame = (fme == null ? Lang.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer, true));
            fplayer.sendMessage(Lang.COMMAND_PERMANENTPOWER_FACTION, blame, change);
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_PERMANENTPOWER_DESCRIPTION.toString();
    }
}
