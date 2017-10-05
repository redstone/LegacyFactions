package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsAutokick extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsAutokick instance = new CmdFactionsAutokick();
	public static CmdFactionsAutokick get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsAutokick() {
		this.aliases.addAll(CommandAliases.cmdAliasesAutokick);

		this.requiredArgs.add("days");
		this.optionalArgs.put("faction", "mine");
		
		this.permission = Permission.AUTOKICK.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		Integer days = this.argAsInt(0);
		
		
		if (days == null) {
			Lang.COMMAND_AUTOKICK_DAYSINVALID.getBuilder()
				.parse()
				.replace("<amount>", Config.autoKickCommandMax)
				.sendTo(this.fme);
			return;
		}
		
		if (days > Config.autoKickCommandMax && !fme.isAdminBypassing()) {
			Lang.COMMAND_AUTOKICK_DAYSINVALID.getBuilder()
				.parse()
				.replace("<amount>", Config.autoKickCommandMax)
				.sendTo(this.fme);
			return;
		}
		
		Faction factionFor = this.fme.getFaction();
		if (this.argIsSet(1)) {
			factionFor = this.argAsFaction(1, null, true);
			
			if (factionFor == null) return;
		}
		
		if (factionFor == this.fme.getFaction()) {
			if (!this.fme.getRole().isAtLeast(Config.autoKickRankMinimum)) {
				Lang.COMMAND_AUTOKICK_BADRANK.getBuilder()
					.parse()
					.replace("<rank>", Config.autoKickRankMinimum.toNiceName())
					.sendTo(this.fme);
				return;
			}
		} else {
			if (!Permission.AUTOKICK_OTHER.has(this.sender)) {
				Lang.COMMAND_AUTOKICK_NOTOTHERS.getBuilder()
					.parse()
					.sendTo(this.fme);
				return;
			}
		}
		
		if (days <= 0) {
			this.fme.getFaction().setAutoKick(-1);
			Lang.COMMAND_AUTOKICK_CLEARED.getBuilder()
				.parse()
				.sendTo(this.fme);
		} else {
			this.fme.getFaction().setAutoKick(days);
			Lang.COMMAND_AUTOKICK_SET.getBuilder()
				.parse()
				.replace("<days>", days)
				.sendTo(this.fme);
		}
		
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_AUTOKICK_DESCRIPTION.toString();
	}
	
}